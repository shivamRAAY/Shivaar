package com.example.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoadmapMonth
import com.example.data.model.SkillTrack
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun RoadmapMasterPoster(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val months by viewModel.roadmapMonths.collectAsState()
    val gates by viewModel.prerequisiteGates.collectAsState()
    val context = LocalContext.current

    var selectedFormat by remember { mutableStateOf("A1_OVERVIEW") } // "A1_OVERVIEW" vs "A4_TILED"
    var zoomLevel by remember { mutableStateOf(1f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("master_poster_screen")
    ) {
        // --- Top Bar: Format Selector, Zoom Controls, and Print Action ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = selectedFormat == "A1_OVERVIEW",
                        onClick = { selectedFormat = "A1_OVERVIEW" },
                        label = { Text("A1/A0 Wall Chart", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("format_a1_btn")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedFormat == "A4_TILED",
                        onClick = { selectedFormat = "A4_TILED" },
                        label = { Text("A4 Tiled Print (4 Years)", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("format_a4_btn")
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Zoom Out
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel - 0.15f).coerceAtLeast(0.7f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "${(zoomLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    // Zoom In
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel + 0.15f).coerceAtMost(1.6f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Print Button
                    Button(
                        onClick = { triggerAndroidPrint(context, months) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("print_poster_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Print / PDF", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // --- Legend Row ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceCardSubtle
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SkillTrack.entries.toTypedArray()) { track ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(track.colorHex))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = track.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // --- Poster Scrollable Canvas ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0F172A)) // High-contrast deep slate wall chart canvas
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (selectedFormat == "A1_OVERVIEW") {
                A1WallChartOverview(months = months, zoom = zoomLevel)
            } else {
                A4TiledPrintView(months = months, zoom = zoomLevel)
            }
        }
    }
}

@Composable
private fun A1WallChartOverview(
    months: List<RoadmapMonth>,
    zoom: Float
) {
    Column(
        modifier = Modifier
            .width((1400 * zoom).dp)
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding((24 * zoom).dp),
        verticalArrangement = Arrangement.spacedBy((20 * zoom).dp)
    ) {
        // Poster Title Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SHIVAAR — 48-MONTH B.TECH CSE MASTER WALL CHART",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Personal Academic, Development, AI & Placement Roadmap • 4 Years • 8 Semesters • 600 Quality DSA Problems",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF334155)
            ) {
                Text(
                    text = "EDITION: AKTU B.TECH CSE CORE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)

        // 4 Years Grid Rows
        (1..4).forEach { year ->
            YearPosterBand(year = year, months = months.filter { it.year == year }, zoom = zoom)
        }
    }
}

@Composable
private fun YearPosterBand(
    year: Int,
    months: List<RoadmapMonth>,
    zoom: Float
) {
    val yearTheme = when (year) {
        1 -> "YEAR 1: FOUNDATIONS (C ➔ C++ ➔ STL ➔ Elementary DSA ➔ 220 Problems)"
        2 -> "YEAR 2: CORE CS & FULL-STACK (JS/TS ➔ React ➔ Node.js ➔ Postgres ➔ OS/DBMS ➔ 400 Problems)"
        3 -> "YEAR 3: SPECIALIZATION & APPLIED AI (2D DP ➔ System Design ➔ RAG ➔ GenAI ➔ 550 Problems)"
        else -> "YEAR 4: PLACEMENTS & CAPSTONE (Company OAs ➔ Interviews ➔ Capstone ➔ 600 Problems)"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding((12 * zoom).dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = yearTheme,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
            Text(
                text = "${months.sumOf { it.estimatedMonthlyHours }} Hours Total",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height((10 * zoom).dp))

        // 12 Months Horizontal Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((8 * zoom).dp)
        ) {
            months.forEach { month ->
                CompactMonthPosterCell(
                    month = month,
                    modifier = Modifier.weight(1f),
                    zoom = zoom
                )
            }
        }
    }
}

@Composable
private fun CompactMonthPosterCell(
    month: RoadmapMonth,
    modifier: Modifier = Modifier,
    zoom: Float
) {
    val trackColor = Color(month.primaryTrack.colorHex)

    Surface(
        modifier = modifier
            .heightIn(min = (160 * zoom).dp),
        shape = RoundedCornerShape((8 * zoom).dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(
            if (month.isCurrentMonth) 2.dp else 1.dp,
            if (month.isCurrentMonth) IndigoPrimaryLight else trackColor.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding((8 * zoom).dp)) {
            // Month # and Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "M${month.monthNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = trackColor
                )
                if (month.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = SuccessEmerald,
                        modifier = Modifier.size((12 * zoom).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height((4 * zoom).dp))

            // Main focus
            Text(
                text = month.mainFocus,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height((4 * zoom).dp))

            // DSA Target
            Text(
                text = "DSA: ${month.cumulativeDsaGoal}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF38BDF8),
                fontSize = 10.sp
            )

            // Hours & Budget
            Text(
                text = "${month.weeklyAllocation.totalWeeklyHours}h/wk • ${month.budget.totalInrEstimate}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height((4 * zoom).dp))

            // Output
            Text(
                text = month.measurableOutput,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCBD5E1),
                fontSize = 9.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun A4TiledPrintView(
    months: List<RoadmapMonth>,
    zoom: Float
) {
    Column(
        modifier = Modifier
            .width((900 * zoom).dp),
        verticalArrangement = Arrangement.spacedBy((24 * zoom).dp)
    ) {
        (1..4).forEach { year ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding((24 * zoom).dp)) {
                    // Alignment Guide & Page Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SHIVAAR CSE ROADMAP • YEAR $year OF 4 (TILED SHEET)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalTextPrimary
                        )
                        Text(
                            text = "PAGE $year / 4 • CUT & ALIGN GUIDE",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = CharcoalTextMuted
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp, modifier = Modifier.padding(vertical = (8 * zoom).dp))

                    val yearMonths = months.filter { it.year == year }

                    // 12 months for this sheet in 2 columns of 6
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy((12 * zoom).dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy((8 * zoom).dp)) {
                            yearMonths.take(6).forEach { m ->
                                TiledMonthCard(month = m, zoom = zoom)
                            }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy((8 * zoom).dp)) {
                            yearMonths.drop(6).forEach { m ->
                                TiledMonthCard(month = m, zoom = zoom)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TiledMonthCard(
    month: RoadmapMonth,
    zoom: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding((10 * zoom).dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Month ${month.monthNumber}: ${month.title}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "DSA: ${month.cumulativeDsaGoal}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
            }

            Text(
                text = "Focus: ${month.mainFocus} • Hours: ${month.weeklyAllocation.totalWeeklyHours}h/wk",
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalTextSecondary
            )

            Text(
                text = "Expected Output: ${month.measurableOutput}",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Checkpoint: ${month.readinessCheckpoint}",
                style = MaterialTheme.typography.labelSmall,
                color = SuccessEmerald,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun triggerAndroidPrint(context: Context, months: List<RoadmapMonth>) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return

    val webView = WebView(context)
    val htmlBuilder = StringBuilder()
    htmlBuilder.append("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; padding: 20px; color: #0f172a; }
                h1 { font-size: 24px; color: #4338ca; border-bottom: 2px solid #e2e8f0; padding-bottom: 8px; }
                .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-top: 16px; page-break-inside: avoid; }
                .card { border: 1px solid #cbd5e1; border-radius: 8px; padding: 10px; background: #f8fafc; font-size: 11px; }
                .card h3 { font-size: 13px; margin: 0 0 4px 0; color: #1e1b4b; }
                .track { color: #4338ca; font-weight: bold; margin-bottom: 4px; }
                .dsa { color: #0284c7; font-weight: bold; }
                .checkpoint { color: #15803d; font-weight: bold; margin-top: 4px; }
                @media print {
                    @page { size: landscape; margin: 10mm; }
                    .page-break { page-break-after: always; }
                }
            </style>
        </head>
        <body>
            <h1>Shivaar — 4-Year B.Tech CSE Master Learning & Placement Roadmap</h1>
            <p>Prepared for Shivam Yadav • B.Tech CSE Core • Lloyd Institute / AKTU</p>
    """.trimIndent())

    (1..4).forEach { year ->
        htmlBuilder.append("<h2>Year $year (Semesters ${year * 2 - 1} & ${year * 2})</h2>")
        htmlBuilder.append("<div class='grid'>")
        months.filter { it.year == year }.forEach { m ->
            htmlBuilder.append("""
                <div class='card'>
                    <div class='track'>M${m.monthNumber} • ${m.primaryTrack.label}</div>
                    <h3>${m.title}</h3>
                    <p><b>Focus:</b> ${m.mainFocus}</p>
                    <p class='dsa'>DSA Goal: ${m.cumulativeDsaGoal} Problems</p>
                    <p><b>Hours:</b> ${m.weeklyAllocation.totalWeeklyHours}h/wk (${m.estimatedMonthlyHours}h/mo)</p>
                    <p><b>Output:</b> ${m.measurableOutput}</p>
                    <p class='checkpoint'>✓ ${m.readinessCheckpoint}</p>
                </div>
            """.trimIndent())
        }
        htmlBuilder.append("</div>")
        if (year < 4) {
            htmlBuilder.append("<div class='page-break'></div>")
        }
    }

    htmlBuilder.append("</body></html>")

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printAdapter = webView.createPrintDocumentAdapter("Shivaar_4Year_CSE_Roadmap")
            printManager.print("Shivaar_4Year_CSE_Roadmap", printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, htmlBuilder.toString(), "text/html", "UTF-8", null)
}
