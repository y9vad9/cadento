package cadento.uikit.material.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Card as M3Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cadento.uikit.component.CardStyle
import cadento.uikit.component.CardVariant

public class Material3CardStyle : CardStyle {
    @Composable
    override fun render(
        modifier: Modifier,
        onClick: (() -> Unit)?,
        variant: CardVariant,
        header: (@Composable () -> Unit)?,
        footer: (@Composable () -> Unit)?,
        content: @Composable () -> Unit
    ) {
        val cardContent: @Composable ColumnScope.() -> Unit = {
            header?.let { it() }
            content()
            footer?.let { it() }
        }

        when (variant) {
            CardVariant.Primary -> {
                if (onClick != null) ElevatedCard(onClick = onClick, modifier = modifier, content = cardContent)
                else ElevatedCard(modifier = modifier, content = cardContent)
            }
            CardVariant.Secondary -> {
                if (onClick != null) OutlinedCard(onClick = onClick, modifier = modifier, content = cardContent)
                else OutlinedCard(modifier = modifier, content = cardContent)
            }
            CardVariant.Tertiary -> {
                if (onClick != null) M3Card(onClick = onClick, modifier = modifier, content = cardContent)
                else M3Card(modifier = modifier, content = cardContent)
            }
        }
    }
}