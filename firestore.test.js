const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read user profiles or tasks", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("tasks").get());
});

// 2. Cross-user isolation checks
test("Authenticated user: Alice cannot read or write Bob's profile", async () => {
  // Seed Bob's data via admin
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      name: "Bob Sharma",
      currentSemester: 1,
      dailyBudgetMinutes: 120,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Alice cannot read Bob's doc
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  // Alice cannot modify Bob's doc
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({ name: "Hacked by Alice" })
  );
});

test("Authenticated user: Alice cannot read or write Bob's tasks or records", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("tasks").doc("task_1").set({
      userId: BOB_UID,
      title: "Bob's DSA Task",
      category: "DSA",
      durationMinutes: 30,
      status: "PENDING",
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Alice cannot read Bob's task
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("tasks").doc("task_1").get());
  // Alice cannot write into Bob's tasks collection
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("tasks").doc("task_2").set({
      userId: BOB_UID,
      title: "Malicious Task",
      category: "DSA",
      durationMinutes: 30,
      status: "PENDING",
    })
  );
});

// 3. User can manage their own data
test("Authenticated user: Alice can create and read her own profile and tasks", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Create profile
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Verma",
      currentSemester: 1,
      dailyBudgetMinutes: 150,
    })
  );

  // Read profile
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Create task
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("tasks").doc("task_alice_1").set({
      userId: ALICE_UID,
      title: "Master C Pointers",
      category: "C_PROGRAMMING",
      durationMinutes: 45,
      status: "PENDING",
    })
  );

  // Read tasks collection
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("tasks").get()
  );
});

// 4. Schema validation prevents corrupt data
test("Authenticated user: Reject invalid task payload", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Invalid status
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("tasks").doc("bad_task").set({
      userId: ALICE_UID,
      title: "Bad Status Task",
      category: "C_PROGRAMMING",
      durationMinutes: 45,
      status: "INVALID_STATUS",
    })
  );

  // Invalid duration
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("tasks").doc("bad_task_2").set({
      userId: ALICE_UID,
      title: "Negative Duration",
      category: "C_PROGRAMMING",
      durationMinutes: -10,
      status: "PENDING",
    })
  );
});
