package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

class ShivaarStorage(context: Context?) {

    private val prefs: SharedPreferences? = try {
        context?.getSharedPreferences("shivaar_storage_prefs", Context.MODE_PRIVATE)
    } catch (e: Exception) {
        null
    }

    fun saveTasks(tasks: List<TaskItem>) {
        if (prefs == null) return
        try {
            val array = JSONArray()
            tasks.forEach { array.put(taskToJson(it)) }
            prefs.edit().putString(KEY_TASKS, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadTasks(): List<TaskItem>? {
        if (prefs == null) return null
        val json = prefs.getString(KEY_TASKS, null) ?: return null
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<TaskItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(jsonToTask(obj))
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    fun saveTomorrowTasks(tasks: List<TaskItem>) {
        if (prefs == null) return
        try {
            val array = JSONArray()
            tasks.forEach { array.put(taskToJson(it)) }
            prefs.edit().putString(KEY_TOMORROW_TASKS, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadTomorrowTasks(): List<TaskItem>? {
        if (prefs == null) return null
        val json = prefs.getString(KEY_TOMORROW_TASKS, null) ?: return null
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<TaskItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(jsonToTask(obj))
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    fun saveCompletionHistory(history: List<TaskCompletionRecord>) {
        if (prefs == null) return
        try {
            val array = JSONArray()
            history.forEach { record ->
                val obj = JSONObject()
                obj.put("id", record.id)
                obj.put("taskId", record.taskId)
                obj.put("taskTitle", record.taskTitle)
                obj.put("category", record.category.name)
                obj.put("completedAt", record.completedAt)
                obj.put("actualMinutes", record.actualMinutes)
                obj.put("notes", record.notes)
                array.put(obj)
            }
            prefs.edit().putString(KEY_COMPLETION_HISTORY, array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadCompletionHistory(): List<TaskCompletionRecord>? {
        if (prefs == null) return null
        val json = prefs.getString(KEY_COMPLETION_HISTORY, null) ?: return null
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<TaskCompletionRecord>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val category = try {
                    TaskCategory.valueOf(obj.optString("category", TaskCategory.C_PROGRAMMING.name))
                } catch (e: Exception) {
                    TaskCategory.C_PROGRAMMING
                }
                list.add(
                    TaskCompletionRecord(
                        id = obj.optString("id", "comp_${System.currentTimeMillis()}_$i"),
                        taskId = obj.optString("taskId", ""),
                        taskTitle = obj.optString("taskTitle", "Completed Task"),
                        category = category,
                        completedAt = obj.optLong("completedAt", System.currentTimeMillis()),
                        actualMinutes = obj.optInt("actualMinutes", 0),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        if (prefs == null) return
        try {
            val obj = JSONObject()
            obj.put("name", profile.name)
            obj.put("degree", profile.degree)
            obj.put("institute", profile.institute)
            obj.put("affiliatingUniversity", profile.affiliatingUniversity)
            obj.put("currentSemester", profile.currentSemester)
            obj.put("currentMonth", profile.currentMonth)
            obj.put("dailyBudgetMinutes", profile.dailyBudgetMinutes)
            obj.put("primaryTrack", profile.primaryTrack)
            obj.put("currentLanguagePriority", profile.currentLanguagePriority)
            prefs.edit().putString(KEY_USER_PROFILE, obj.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadUserProfile(): UserProfile? {
        if (prefs == null) return null
        val json = prefs.getString(KEY_USER_PROFILE, null) ?: return null
        return try {
            val obj = JSONObject(json)
            UserProfile(
                name = obj.optString("name", "Shivam Yadav"),
                degree = obj.optString("degree", "B.Tech CSE Core"),
                institute = obj.optString("institute", "Lloyd Institute of Engineering & Technology"),
                affiliatingUniversity = obj.optString("affiliatingUniversity", "AKTU (Uttar Pradesh)"),
                currentSemester = obj.optInt("currentSemester", 1),
                currentMonth = obj.optInt("currentMonth", 1),
                dailyBudgetMinutes = obj.optInt("dailyBudgetMinutes", 150),
                primaryTrack = obj.optString("primaryTrack", "Full-Stack Development & Applied AI Engineering"),
                currentLanguagePriority = obj.optString("currentLanguagePriority", "Complete C Fundamentals & Master AKTU Python")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun taskToJson(task: TaskItem): JSONObject {
        val obj = JSONObject()
        obj.put("id", task.id)
        obj.put("title", task.title)
        obj.put("category", task.category.name)
        obj.put("whyItMatters", task.whyItMatters)
        obj.put("instructions", task.instructions)
        obj.put("durationMinutes", task.durationMinutes)
        obj.put("priority", task.priority.name)
        obj.put("difficulty", task.difficulty.name)
        obj.put("resourceName", task.resourceName)
        obj.put("resourceUrl", task.resourceUrl)
        obj.put("definitionOfDone", task.definitionOfDone)
        obj.put("status", task.status.name)
        obj.put("scheduledDate", task.scheduledDate)
        if (task.completedAt != null) {
            obj.put("completedAt", task.completedAt)
        }
        obj.put("userNotes", task.userNotes)
        obj.put("actualMinutes", task.actualMinutes)
        if (task.blockerReason != null) {
            obj.put("blockerReason", task.blockerReason)
        }
        return obj
    }

    private fun jsonToTask(obj: JSONObject): TaskItem {
        val category = try {
            TaskCategory.valueOf(obj.optString("category", TaskCategory.C_PROGRAMMING.name))
        } catch (e: Exception) {
            TaskCategory.C_PROGRAMMING
        }
        val priority = try {
            Priority.valueOf(obj.optString("priority", Priority.MEDIUM.name))
        } catch (e: Exception) {
            Priority.MEDIUM
        }
        val difficulty = try {
            Difficulty.valueOf(obj.optString("difficulty", Difficulty.BEGINNER.name))
        } catch (e: Exception) {
            Difficulty.BEGINNER
        }
        val status = try {
            TaskStatus.valueOf(obj.optString("status", TaskStatus.PENDING.name))
        } catch (e: Exception) {
            TaskStatus.PENDING
        }

        return TaskItem(
            id = obj.optString("id", "task_${System.currentTimeMillis()}"),
            title = obj.optString("title", "Engineering Task"),
            category = category,
            whyItMatters = obj.optString("whyItMatters", ""),
            instructions = obj.optString("instructions", ""),
            durationMinutes = obj.optInt("durationMinutes", 30),
            priority = priority,
            difficulty = difficulty,
            resourceName = obj.optString("resourceName", ""),
            resourceUrl = obj.optString("resourceUrl", ""),
            definitionOfDone = obj.optString("definitionOfDone", ""),
            status = status,
            scheduledDate = obj.optString("scheduledDate", "Today"),
            completedAt = if (obj.has("completedAt")) obj.optLong("completedAt") else null,
            userNotes = obj.optString("userNotes", ""),
            actualMinutes = obj.optInt("actualMinutes", 0),
            blockerReason = if (obj.has("blockerReason")) obj.optString("blockerReason") else null
        )
    }

    fun saveOnboardingData(data: OnboardingData) {
        if (prefs == null) return
        try {
            val obj = JSONObject()
            obj.put("currentStep", data.currentStep)
            obj.put("isCompleted", data.isCompleted)
            if (data.completedAt != null) obj.put("completedAt", data.completedAt)
            if (data.chosenPathId != null) obj.put("chosenPathId", data.chosenPathId)

            // Step 1: Education
            val eduObj = JSONObject().apply {
                put("qualification", data.step1Education.qualification)
                put("stage", data.step1Education.stage.name)
                put("fieldOfStudy", data.step1Education.fieldOfStudy)
                put("currentYear", data.step1Education.currentYear ?: 1)
                put("currentSemester", data.step1Education.currentSemester ?: 1)
                put("institution", data.step1Education.institution)
                put("curriculum", data.step1Education.curriculum)
                put("expectedGraduationDate", data.step1Education.expectedGraduationDate)
                val subArray = JSONArray()
                data.step1Education.studiedSubjects.forEach { subArray.put(it) }
                put("studiedSubjects", subArray)
            }
            obj.put("education", eduObj)

            // Step 2: Skills
            val skillObj = JSONObject().apply {
                val langArray = JSONArray()
                data.step2Skills.languagesKnown.forEach { langArray.put(it) }
                put("languagesKnown", langArray)
                val topicArray = JSONArray()
                data.step2Skills.topicsCompleted.forEach { topicArray.put(it) }
                put("topicsCompleted", topicArray)
                val projArray = JSONArray()
                data.step2Skills.projectsBuilt.forEach { projArray.put(it) }
                put("projectsBuilt", projArray)
                put("dsaExperience", data.step2Skills.dsaExperience)
                put("devExperience", data.step2Skills.devExperience)
                put("aiExperience", data.step2Skills.aiExperience)
                put("portfolioOrGithubUrl", data.step2Skills.portfolioOrGithubUrl)
                put("confidenceLevel", data.step2Skills.confidenceLevel.name)
            }
            obj.put("skills", skillObj)

            // Step 3: Goals
            val goalObj = JSONObject().apply {
                val goalsArray = JSONArray()
                data.step3Goals.selectedGoals.forEach { goalsArray.put(it.name) }
                put("selectedGoals", goalsArray)
                put("customGoal", data.step3Goals.customGoal)
                put("targetTimelineMonths", data.step3Goals.targetTimelineMonths)
                val rolesArray = JSONArray()
                data.step3Goals.preferredRoles.forEach { rolesArray.put(it) }
                put("preferredRoles", rolesArray)
                put("primaryPriority", data.step3Goals.primaryPriority)
            }
            obj.put("goals", goalObj)

            // Step 4: Interests
            val intObj = JSONObject().apply {
                val intArray = JSONArray()
                data.step4Interests.interests.forEach { intArray.put(it.name) }
                put("interests", intArray)
            }
            obj.put("interests", intObj)

            // Step 5: Constraints
            val constObj = JSONObject().apply {
                put("availableHoursPerWeek", data.step5Constraints.availableHoursPerWeek)
                put("preferredSchedule", data.step5Constraints.preferredSchedule)
                put("budgetRangeInr", data.step5Constraints.budgetRangeInr)
                put("preferredLanguage", data.step5Constraints.preferredLanguage)
                put("deviceConstraints", data.step5Constraints.deviceConstraints)
                put("upcomingExamsOrDeadlines", data.step5Constraints.upcomingExamsOrDeadlines)
                put("existingCommitments", data.step5Constraints.existingCommitments)
                val prefArray = JSONArray()
                data.step5Constraints.learningStylePreferences.forEach { prefArray.put(it) }
                put("learningStylePreferences", prefArray)
            }
            obj.put("constraints", constObj)

            prefs.edit().putString(KEY_ONBOARDING_DATA, obj.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadOnboardingData(): OnboardingData? {
        if (prefs == null) return null
        val json = prefs.getString(KEY_ONBOARDING_DATA, null) ?: return null
        return try {
            val obj = JSONObject(json)
            val currentStep = obj.optInt("currentStep", 1)
            val isCompleted = obj.optBoolean("isCompleted", false)
            val completedAt = if (obj.has("completedAt")) obj.optLong("completedAt") else null
            val chosenPathId = if (obj.has("chosenPathId")) obj.optString("chosenPathId") else null

            // Education
            val eduObj = obj.optJSONObject("education")
            val education = if (eduObj != null) {
                val stage = try {
                    EducationStage.valueOf(eduObj.optString("stage", EducationStage.UNDERGRADUATE.name))
                } catch (_: Exception) {
                    EducationStage.UNDERGRADUATE
                }
                val subs = mutableListOf<String>()
                val subArr = eduObj.optJSONArray("studiedSubjects")
                if (subArr != null) {
                    for (i in 0 until subArr.length()) subs.add(subArr.getString(i))
                }
                OnboardingEducation(
                    qualification = eduObj.optString("qualification", "Undergraduate"),
                    stage = stage,
                    fieldOfStudy = eduObj.optString("fieldOfStudy", "Computer Science & Engineering"),
                    currentYear = eduObj.optInt("currentYear", 1),
                    currentSemester = eduObj.optInt("currentSemester", 1),
                    institution = eduObj.optString("institution", "Lloyd Institute of Engineering & Technology"),
                    curriculum = eduObj.optString("curriculum", "AKTU (Uttar Pradesh)"),
                    expectedGraduationDate = eduObj.optString("expectedGraduationDate", "2028"),
                    studiedSubjects = if (subs.isNotEmpty()) subs else listOf("Programming in C", "Engineering Mathematics")
                )
            } else OnboardingEducation()

            // Skills
            val skillObj = obj.optJSONObject("skills")
            val skills = if (skillObj != null) {
                val langs = mutableListOf<String>()
                val langArr = skillObj.optJSONArray("languagesKnown")
                if (langArr != null) {
                    for (i in 0 until langArr.length()) langs.add(langArr.getString(i))
                }
                val topics = mutableListOf<String>()
                val topArr = skillObj.optJSONArray("topicsCompleted")
                if (topArr != null) {
                    for (i in 0 until topArr.length()) topics.add(topArr.getString(i))
                }
                val projs = mutableListOf<String>()
                val projArr = skillObj.optJSONArray("projectsBuilt")
                if (projArr != null) {
                    for (i in 0 until projArr.length()) projs.add(projArr.getString(i))
                }
                val conf = try {
                    SkillConfidence.valueOf(skillObj.optString("confidenceLevel", SkillConfidence.FAMILIAR.name))
                } catch (_: Exception) {
                    SkillConfidence.FAMILIAR
                }
                OnboardingSkills(
                    languagesKnown = if (langs.isNotEmpty()) langs else listOf("C", "Python"),
                    topicsCompleted = topics,
                    projectsBuilt = projs,
                    dsaExperience = skillObj.optString("dsaExperience", "Arrays and Strings"),
                    devExperience = skillObj.optString("devExperience", "Basic Web Frontend"),
                    aiExperience = skillObj.optString("aiExperience", "Prompt Engineering"),
                    portfolioOrGithubUrl = skillObj.optString("portfolioOrGithubUrl", ""),
                    confidenceLevel = conf
                )
            } else OnboardingSkills()

            // Goals
            val goalObj = obj.optJSONObject("goals")
            val goals = if (goalObj != null) {
                val selGoals = mutableListOf<CareerGoal>()
                val gArr = goalObj.optJSONArray("selectedGoals")
                if (gArr != null) {
                    for (i in 0 until gArr.length()) {
                        try {
                            selGoals.add(CareerGoal.valueOf(gArr.getString(i)))
                        } catch (_: Exception) {}
                    }
                }
                val roles = mutableListOf<String>()
                val rArr = goalObj.optJSONArray("preferredRoles")
                if (rArr != null) {
                    for (i in 0 until rArr.length()) roles.add(rArr.getString(i))
                }
                OnboardingGoals(
                    selectedGoals = if (selGoals.isNotEmpty()) selGoals else listOf(CareerGoal.SWE_PLACEMENT),
                    customGoal = goalObj.optString("customGoal", ""),
                    targetTimelineMonths = goalObj.optInt("targetTimelineMonths", 24),
                    preferredRoles = roles,
                    primaryPriority = goalObj.optString("primaryPriority", "Build solid engineering fundamentals")
                )
            } else OnboardingGoals()

            // Interests
            val intObj = obj.optJSONObject("interests")
            val interests = if (intObj != null) {
                val selInt = mutableListOf<InterestArea>()
                val iArr = intObj.optJSONArray("interests")
                if (iArr != null) {
                    for (i in 0 until iArr.length()) {
                        try {
                            selInt.add(InterestArea.valueOf(iArr.getString(i)))
                        } catch (_: Exception) {}
                    }
                }
                OnboardingInterests(
                    interests = if (selInt.isNotEmpty()) selInt else listOf(InterestArea.BUILDING_APPS, InterestArea.SOLVING_ALGORITHMS)
                )
            } else OnboardingInterests()

            // Constraints
            val constObj = obj.optJSONObject("constraints")
            val constraints = if (constObj != null) {
                val prefsList = mutableListOf<String>()
                val pArr = constObj.optJSONArray("learningStylePreferences")
                if (pArr != null) {
                    for (i in 0 until pArr.length()) prefsList.add(pArr.getString(i))
                }
                OnboardingConstraints(
                    availableHoursPerWeek = constObj.optInt("availableHoursPerWeek", 18),
                    preferredSchedule = constObj.optString("preferredSchedule", "Evenings & Weekends"),
                    budgetRangeInr = constObj.optString("budgetRangeInr", "Free resources only (₹0)"),
                    preferredLanguage = constObj.optString("preferredLanguage", "English"),
                    deviceConstraints = constObj.optString("deviceConstraints", "Standard Laptop"),
                    upcomingExamsOrDeadlines = constObj.optString("upcomingExamsOrDeadlines", ""),
                    learningStylePreferences = prefsList,
                    existingCommitments = constObj.optString("existingCommitments", "")
                )
            } else OnboardingConstraints()

            OnboardingData(
                step1Education = education,
                step2Skills = skills,
                step3Goals = goals,
                step4Interests = interests,
                step5Constraints = constraints,
                chosenPathId = chosenPathId,
                currentStep = currentStep,
                isCompleted = isCompleted,
                completedAt = completedAt
            )
        } catch (e: Exception) {
            null
        }
    }

    fun saveSavedRoadmapIds(ids: Set<String>) {
        if (prefs == null) return
        prefs.edit().putStringSet(KEY_SAVED_ROADMAP_IDS, ids).apply()
    }

    fun loadSavedRoadmapIds(): Set<String> {
        if (prefs == null) return emptySet()
        return prefs.getStringSet(KEY_SAVED_ROADMAP_IDS, emptySet()) ?: emptySet()
    }

    fun saveFollowedRoadmapIds(ids: Set<String>) {
        if (prefs == null) return
        prefs.edit().putStringSet(KEY_FOLLOWED_ROADMAP_IDS, ids).apply()
    }

    fun loadFollowedRoadmapIds(): Set<String> {
        if (prefs == null) return emptySet()
        return prefs.getStringSet(KEY_FOLLOWED_ROADMAP_IDS, emptySet()) ?: emptySet()
    }

    fun saveAdoptedRoadmapId(id: String?) {
        if (prefs == null) return
        if (id != null) {
            prefs.edit().putString(KEY_ADOPTED_ROADMAP_ID, id).apply()
        } else {
            prefs.edit().remove(KEY_ADOPTED_ROADMAP_ID).apply()
        }
    }

    fun loadAdoptedRoadmapId(): String? {
        if (prefs == null) return null
        return prefs.getString(KEY_ADOPTED_ROADMAP_ID, null)
    }

    fun saveMilestoneCustomNotes(notes: Map<String, String>) {
        if (prefs == null) return
        try {
            val obj = JSONObject()
            notes.forEach { (k, v) -> obj.put(k, v) }
            prefs.edit().putString(KEY_MILESTONE_NOTES, obj.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadMilestoneCustomNotes(): Map<String, String> {
        if (prefs == null) return emptyMap()
        val json = prefs.getString(KEY_MILESTONE_NOTES, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(json)
            val map = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = obj.getString(key)
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun saveMilestoneCompletedExercises(completedMap: Map<String, List<Int>>) {
        if (prefs == null) return
        try {
            val obj = JSONObject()
            completedMap.forEach { (k, list) ->
                val arr = JSONArray()
                list.forEach { arr.put(it) }
                obj.put(k, arr)
            }
            prefs.edit().putString(KEY_MILESTONE_EXERCISES, obj.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadMilestoneCompletedExercises(): Map<String, List<Int>> {
        if (prefs == null) return emptyMap()
        val json = prefs.getString(KEY_MILESTONE_EXERCISES, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(json)
            val map = mutableMapOf<String, List<Int>>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val arr = obj.getJSONArray(key)
                val list = mutableListOf<Int>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getInt(i))
                }
                map[key] = list
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    companion object {
        private const val KEY_TASKS = "shivaar_tasks_v1"
        private const val KEY_TOMORROW_TASKS = "shivaar_tomorrow_tasks_v1"
        private const val KEY_COMPLETION_HISTORY = "shivaar_completion_history_v1"
        private const val KEY_USER_PROFILE = "shivaar_user_profile_v1"
        private const val KEY_ONBOARDING_DATA = "shivaar_onboarding_data_v1"
        private const val KEY_SAVED_ROADMAP_IDS = "shivaar_saved_roadmaps_v1"
        private const val KEY_FOLLOWED_ROADMAP_IDS = "shivaar_followed_roadmaps_v1"
        private const val KEY_ADOPTED_ROADMAP_ID = "shivaar_adopted_roadmap_id_v1"
        private const val KEY_MILESTONE_NOTES = "shivaar_milestone_notes_v1"
        private const val KEY_MILESTONE_EXERCISES = "shivaar_milestone_exercises_v1"
    }
}
