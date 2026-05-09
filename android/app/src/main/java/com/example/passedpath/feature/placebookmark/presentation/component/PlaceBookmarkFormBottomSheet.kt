package com.example.passedpath.feature.placebookmark.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passedpath.R
import com.example.passedpath.feature.place.domain.model.BookmarkPlaceType
import com.example.passedpath.ui.component.button.BaseButton
import com.example.passedpath.ui.component.input.BaseInputButton
import com.example.passedpath.ui.component.input.BaseInputField
import com.example.passedpath.ui.component.modal.PassedPathBottomModal
import com.example.passedpath.ui.component.place.PlaceNameMarker
import com.example.passedpath.ui.theme.Gray200
import com.example.passedpath.ui.theme.Gray400
import com.example.passedpath.ui.theme.Gray900
import com.example.passedpath.ui.theme.Green50
import com.example.passedpath.ui.theme.Green100
import com.example.passedpath.ui.theme.Green500
import com.example.passedpath.ui.theme.White

@Composable
internal fun PlaceBookmarkFormOverlay(
    title: String,
    submitText: String,
    placeNameMarker: String?,
    placeName: String,
    roadAddress: String,
    selectedType: BookmarkPlaceType,
    isSubmitting: Boolean,
    isSubmitEnabled: Boolean,
    isNameFocused: Boolean,
    onPlaceNameChange: (String) -> Unit,
    onNameFocusChanged: (Boolean) -> Unit,
    onTypeSelected: (BookmarkPlaceType) -> Unit,
    onClearInputFocus: () -> Unit,
    onAddressClick: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    PassedPathBottomModal(
        onDimClick = onClearInputFocus,
        modifier = modifier,
        onBackPress = {
            if (isNameFocused) {
                onClearInputFocus()
            } else {
                onClearInputFocus()
                onDismiss()
            }
        }
    ) {
        PlaceBookmarkFormBottomSheet(
            title = title,
            submitText = submitText,
            placeNameMarker = placeNameMarker,
            placeName = placeName,
            roadAddress = roadAddress,
            selectedType = selectedType,
            isSubmitting = isSubmitting,
            isSubmitEnabled = isSubmitEnabled,
            onPlaceNameChange = onPlaceNameChange,
            onNameFocusChanged = onNameFocusChanged,
            onTypeSelected = onTypeSelected,
            onClearInputFocus = onClearInputFocus,
            onAddressClick = onAddressClick,
            onDismiss = onDismiss,
            onSubmit = onSubmit
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PlaceBookmarkFormBottomSheet(
    title: String,
    submitText: String,
    placeNameMarker: String?,
    placeName: String,
    roadAddress: String,
    selectedType: BookmarkPlaceType,
    isSubmitting: Boolean,
    isSubmitEnabled: Boolean,
    onPlaceNameChange: (String) -> Unit,
    onNameFocusChanged: (Boolean) -> Unit,
    onTypeSelected: (BookmarkPlaceType) -> Unit,
    onClearInputFocus: () -> Unit,
    onAddressClick: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetInteractionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val hasPlaceNameMarker = !placeNameMarker.isNullOrBlank()

    fun clearSheetInputFocus() {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onClearInputFocus()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = White,
        tonalElevation = 0.dp,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .clickable(
                    interactionSource = sheetInteractionSource,
                    indication = null,
                    onClick = ::clearSheetInputFocus
                )
                .padding(
                    start = 20.dp,
                    top = if (hasPlaceNameMarker) 34.dp else 26.dp,
                    end = 20.dp,
                    bottom = 40.dp
                )
        ) {
            if (hasPlaceNameMarker) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    PlaceNameMarker(placeName = placeNameMarker.orEmpty())
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium,
                    color = Gray900,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(
                    onClick = {
                        clearSheetInputFocus()
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PlaceBookmarkFieldLabel(text = stringResource(R.string.place_bookmark_name_label))
            Spacer(modifier = Modifier.height(10.dp))
            BaseInputField(
                value = placeName,
                onValueChange = onPlaceNameChange,
                placeholder = stringResource(R.string.place_bookmark_name_label),
                singleLine = true,
                imeAction = ImeAction.Done,
                onFocusChanged = onNameFocusChanged,
                onImeAction = ::clearSheetInputFocus
            )
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                text = stringResource(R.string.place_bookmark_name_helper),
                style = MaterialTheme.typography.bodySmall,
                color = Green500,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(22.dp))
            PlaceBookmarkFieldLabel(text = stringResource(R.string.place_bookmark_address_label))
            Spacer(modifier = Modifier.height(10.dp))
            BaseInputButton(
                text = roadAddress,
                onClick = {
                    clearSheetInputFocus()
                    onAddressClick()
                },
                placeholder = stringResource(R.string.place_bookmark_address_label),
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Green500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(22.dp))
            PlaceBookmarkFieldLabel(text = stringResource(R.string.place_bookmark_type_label))
            Spacer(modifier = Modifier.height(12.dp))
            PlaceBookmarkTypeSelector(
                selectedType = selectedType,
                onTypeSelected = { type ->
                    clearSheetInputFocus()
                    onTypeSelected(type)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            BaseButton(
                text = submitText,
                onClick = {
                    clearSheetInputFocus()
                    onSubmit()
                },
                enabled = isSubmitEnabled && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }
    }
}

@Composable
private fun PlaceBookmarkFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Gray400,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
}

@Composable
private fun PlaceBookmarkTypeSelector(
    selectedType: BookmarkPlaceType,
    onTypeSelected: (BookmarkPlaceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BookmarkPlaceType.entries.forEach { type ->
            PlaceBookmarkTypeOption(
                type = type,
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PlaceBookmarkTypeOption(
    type: BookmarkPlaceType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Green50 else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) Green100 else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) Green500 else Gray200,
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            PlaceBookmarkBadge(
                type = type,
                size = 36.dp,
                iconSize = 18.dp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) Green500 else Gray400,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private val BookmarkPlaceType.displayName: String
    get() = when (this) {
        BookmarkPlaceType.HOME -> "집"
        BookmarkPlaceType.COMPANY -> "회사"
        BookmarkPlaceType.SCHOOL -> "학교"
        BookmarkPlaceType.ETC -> "기타"
    }
