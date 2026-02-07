package cadento.uikit.unstyled.desktop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cadento.uikit.component.TextInputStyle
import cadento.uikit.component.TextLevel
import cadento.uikit.component.Text
import cadento.uikit.unstyled.desktop.theme.UnstyledDesktopColors

private val CornerRadius = 6.dp
private val TextInputMinHeight = 36.dp
private val TextInputBorderWidth = 1.dp
private val TextInputHorizontalPadding = 12.dp
private val TextInputVerticalPadding = 8.dp
private val LabelBottomPadding = 4.dp
private val FeedbackTopPadding = 4.dp

/**
 * Unstyled Desktop implementation of [TextInputStyle].
 */
public class UnstyledDesktopTextInputStyle(private val colors: UnstyledDesktopColors) : TextInputStyle {
    @Composable
    override fun render(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier,
        label: String?,
        placeholder: String?,
        supportingText: String?,
        error: String?,
        leadingIcon: (@Composable () -> Unit)?,
        trailingIcon: (@Composable () -> Unit)?,
        isEnabled: Boolean,
        isReadOnly: Boolean,
        isMultiline: Boolean,
        maxLines: Int
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        
        val borderColor = when {
            error != null -> colors.error
            isFocused -> colors.accent
            else -> colors.border
        }

        Column(modifier = modifier) {
            label?.let {
                Text(text = it, level = TextLevel.Label, modifier = Modifier.padding(bottom = LabelBottomPadding))
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = TextInputMinHeight)
                    .border(TextInputBorderWidth, borderColor, RoundedCornerShape(CornerRadius))
                    .padding(horizontal = TextInputHorizontalPadding, vertical = TextInputVerticalPadding),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    leadingIcon?.let { it() }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(text = placeholder, level = TextLevel.Body, modifier = Modifier.background(Color.Transparent))
                        }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            enabled = isEnabled,
                            readOnly = isReadOnly,
                            singleLine = !isMultiline,
                            maxLines = maxLines,
                            interactionSource = interactionSource,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = colors.onSurface,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    trailingIcon?.let { it() }
                }
            }

            val feedbackText = error ?: supportingText
            feedbackText?.let {
                Text(
                    text = it,
                    level = TextLevel.Caption,
                    modifier = Modifier.padding(top = FeedbackTopPadding)
                )
            }
        }
    }
}
