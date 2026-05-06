package com.example.passedpath.feature.calendar.presentation.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.feature.calendar.presentation.model.CalendarDayStatus
import com.example.passedpath.feature.calendar.presentation.model.CalendarMonthCell
import com.example.passedpath.feature.calendar.presentation.model.buildCalendarMonthCells
import com.example.passedpath.feature.calendar.presentation.model.toCalendarDateKey
import com.example.passedpath.ui.component.button.BaseButton
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray500
import com.example.passedpath.ui.theme.Gray900
import com.example.passedpath.ui.theme.Green100
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.PassedPathTheme
import com.example.passedpath.ui.theme.White
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun CalendarRoute(
    initialDateKey: String,
    onBackClick: () -> Unit,
    onDateConfirmed: (String) -> Unit,
    onFavoriteListClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initialDate = remember(initialDateKey) { parseDateOrToday(initialDateKey) }
    var anchorDateKey by rememberSaveable(initialDateKey) {
        mutableStateOf(initialDate.toCalendarDateKey())
    }
    var selectedDateKey by rememberSaveable(initialDateKey) {
        mutableStateOf<String?>(null)
    }
    val anchorDate = remember(anchorDateKey) { parseDateOrToday(anchorDateKey) }
    val selectedDate = remember(selectedDateKey) {
        selectedDateKey?.let(::parseDateOrToday)
    }
    val visibleMonth = remember(anchorDate) { YearMonth.from(anchorDate) }

    CalendarScreen(
        anchorDate = anchorDate,
        visibleMonth = visibleMonth,
        selectedDate = selectedDate,
        dayStatuses = emptyMap(),
        onBackClick = onBackClick,
        onDatePickedFromSystem = { pickedDate ->
            anchorDateKey = pickedDate.toCalendarDateKey()
            selectedDateKey = null
        },
        onPreviousMonthClick = {
            anchorDateKey = anchorDate.shiftMonth(-1).toCalendarDateKey()
            selectedDateKey = null
        },
        onNextMonthClick = {
            anchorDateKey = anchorDate.shiftMonth(1).toCalendarDateKey()
            selectedDateKey = null
        },
        onDateClick = { date ->
            anchorDateKey = date.toCalendarDateKey()
            selectedDateKey = date.toCalendarDateKey()
        },
        onFavoriteListClick = onFavoriteListClick,
        onMoreClick = onMoreClick,
        onConfirmClick = {
            selectedDateKey?.let(onDateConfirmed)
        },
        modifier = modifier
    )
}

@Composable
private fun CalendarScreen(
    anchorDate: LocalDate,
    visibleMonth: YearMonth,
    selectedDate: LocalDate?,
    dayStatuses: Map<LocalDate, CalendarDayStatus>,
    onBackClick: () -> Unit,
    onDatePickedFromSystem: (LocalDate) -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    onFavoriteListClick: () -> Unit,
    onMoreClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cells = remember(visibleMonth, dayStatuses) {
        buildCalendarMonthCells(
            visibleMonth = visibleMonth,
            dayStatuses = dayStatuses
        )
    }
    val selectedDateKey = selectedDate?.toCalendarDateKey()
    val ctaText = selectedDateKey?.let { dateKey ->
        stringResource(R.string.calendar_record_view_cta, dateKey)
    } ?: stringResource(R.string.calendar_no_date_selected_cta)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding()
    ) {
        CalendarTopBar(
            anchorDate = anchorDate,
            onBackClick = onBackClick,
            onDateClick = {
                showCalendarDatePicker(
                    context = context,
                    initialDate = anchorDate,
                    onDatePicked = onDatePickedFromSystem
                )
            },
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick,
            onFavoriteListClick = onFavoriteListClick,
            onMoreClick = onMoreClick
        )

        Spacer(modifier = Modifier.height(52.dp))

        WeekdayHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        CalendarMonthGrid(
            cells = cells,
            today = LocalDate.now(),
            selectedDate = selectedDate,
            onDateClick = onDateClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        CalendarLegend(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, end = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        BaseButton(
            text = ctaText,
            onClick = onConfirmClick,
            enabled = selectedDate != null,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 20.dp),
            textFontSize = 15.sp,
            textFontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CalendarTopBar(
    anchorDate: LocalDate,
    onBackClick: () -> Unit,
    onDateClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onFavoriteListClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.calendar_back),
                tint = Gray900,
                modifier = Modifier.size(18.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(start = 46.dp, end = 88.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MonthArrowButton(
                iconResId = R.drawable.ic_arrow_left,
                contentDescription = stringResource(R.string.calendar_previous_month),
                onClick = onPreviousMonthClick
            )
            CalendarDateTitle(
                anchorDate = anchorDate,
                onClick = onDateClick,
                modifier = Modifier.weight(1f)
            )
            MonthArrowButton(
                iconResId = R.drawable.ic_arrow_right,
                contentDescription = stringResource(R.string.calendar_next_month),
                onClick = onNextMonthClick
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onFavoriteListClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_list),
                    contentDescription = stringResource(R.string.calendar_favorite_list),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(width = 27.dp, height = 17.dp)
                )
            }
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vert),
                    contentDescription = stringResource(R.string.main_more),
                    tint = Gray400,
                    modifier = Modifier.size(width = 4.dp, height = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun CalendarDateTitle(
    anchorDate: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        Text(
            text = anchorDate.format(CalendarHeaderFormatter),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = null,
            tint = Gray900,
            modifier = Modifier.size(width = 12.dp, height = 7.dp)
        )
    }
}

@Composable
private fun MonthArrowButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            tint = Gray400,
            modifier = Modifier.size(width = 7.dp, height = 12.dp)
        )
    }
}

@Composable
private fun WeekdayHeader(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        weekdayLabels().forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Gray400,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    cells: List<CalendarMonthCell>,
    today: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cells.chunked(7).forEach { weekCells ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekCells.forEach { cell ->
                    CalendarDayCell(
                        cell = cell,
                        isToday = cell.date == today,
                        isSelected = cell.date == selectedDate,
                        onDateClick = onDateClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    cell: CalendarMonthCell,
    isToday: Boolean,
    isSelected: Boolean,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = cell.date
    Box(
        modifier = modifier.height(44.dp),
        contentAlignment = Alignment.Center
    ) {
        if (date == null) return@Box

        val dateColor = when {
            isSelected -> White
            date.dayOfWeek == java.time.DayOfWeek.SUNDAY -> SundayColor
            date.dayOfWeek == java.time.DayOfWeek.SATURDAY -> SaturdayColor
            else -> Gray900
        }
        val backgroundColor = when {
            isSelected -> Green500
            isToday -> Green100
            else -> Color.Transparent
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable { onDateClick(date) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = dateColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        CalendarDayIndicators(
            status = cell.status,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 1.dp)
        )
    }
}

@Composable
private fun CalendarDayIndicators(
    status: CalendarDayStatus,
    modifier: Modifier = Modifier
) {
    val colors = buildList {
        if (status.hasManualData) add(ManualDataColor)
        if (status.hasLocationData) add(LocationDataColor)
        if (status.isBookmarked) add(FavoriteColor)
    }
    if (colors.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun CalendarLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarLegendItem(
            color = ManualDataColor,
            text = stringResource(R.string.calendar_manual_data)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CalendarLegendItem(
            color = LocationDataColor,
            text = stringResource(R.string.calendar_location_data)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CalendarLegendItem(
            color = FavoriteColor,
            text = stringResource(R.string.calendar_favorite)
        )
    }
}

@Composable
private fun CalendarLegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            color = Gray400,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun weekdayLabels(): List<String> {
    val weekdays = java.text.DateFormatSymbols(Locale.KOREAN).shortWeekdays
    return (1..7).map { index -> weekdays[index] }
}

private fun showCalendarDatePicker(
    context: android.content.Context,
    initialDate: LocalDate,
    onDatePicked: (LocalDate) -> Unit
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDatePicked(LocalDate.of(year, month + 1, dayOfMonth))
        },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    ).show()
}

private fun parseDateOrToday(dateKey: String): LocalDate {
    return runCatching { LocalDate.parse(dateKey) }.getOrDefault(LocalDate.now())
}

private fun LocalDate.shiftMonth(monthDelta: Long): LocalDate {
    val nextMonth = YearMonth.from(this).plusMonths(monthDelta)
    val nextDayOfMonth = min(dayOfMonth, nextMonth.lengthOfMonth())
    return nextMonth.atDay(nextDayOfMonth)
}

private val CalendarHeaderFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd. EEE", Locale.KOREAN)

private val SundayColor = Color(0xFFFF0000)
private val SaturdayColor = Color(0xFF0057FF)
private val ManualDataColor = Color(0xFF147B82)
private val LocationDataColor = Color(0xFF94D8DE)
private val FavoriteColor = Color(0xFFFFC95C)

@Preview(showBackground = true, widthDp = 393, heightDp = 760)
@Composable
private fun CalendarScreenPreview() {
    val previewMonth = YearMonth.of(2026, 1)
    val previewStatuses = mapOf(
        LocalDate.of(2026, 1, 3) to CalendarDayStatus(isBookmarked = true),
        LocalDate.of(2026, 1, 6) to CalendarDayStatus(hasManualData = true),
        LocalDate.of(2026, 1, 13) to CalendarDayStatus(
            hasManualData = true,
            hasLocationData = true,
            isBookmarked = true
        ),
        LocalDate.of(2026, 1, 14) to CalendarDayStatus(isBookmarked = true),
        LocalDate.of(2026, 1, 20) to CalendarDayStatus(
            hasManualData = true,
            hasLocationData = true
        )
    )

    PassedPathTheme {
        CalendarScreen(
            anchorDate = LocalDate.of(2026, 1, 20),
            visibleMonth = previewMonth,
            selectedDate = null,
            dayStatuses = previewStatuses,
            onBackClick = {},
            onDatePickedFromSystem = {},
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            onDateClick = {},
            onFavoriteListClick = {},
            onMoreClick = {},
            onConfirmClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
