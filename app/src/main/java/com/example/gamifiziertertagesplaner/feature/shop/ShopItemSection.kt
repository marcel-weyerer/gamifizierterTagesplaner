package com.example.gamifiziertertagesplaner.feature.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Represents the shopping interaction section for a single item type
 *
 * @param item                The name of the item
 * @param painterResource     The painter resource for the item icon
 * @param price               The price of the item
 * @param itemAmount          The current amount of the item in the basket
 * @param onIncreaseAmount    Callback for increasing the amount of the item
 * @param onReduceAmount      Callback for reducing the amount of the item
 */
@Composable
fun ItemShopSection(
  item: String,
  painterResource: Painter,
  price: Int,
  itemAmount: Int,
  onIncreaseAmount: () -> Unit,
  onReduceAmount: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp, horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    ItemShopButton(
      modifier = Modifier.weight(1f),
      painterResource = painterResource,
      item = item,
      price = price,
      onClick = onIncreaseAmount
    )

    if (itemAmount > 0) {
      ItemAmount(
        itemAmount = itemAmount,
        onIncreaseAmount = onIncreaseAmount,
        onReduceAmount = onReduceAmount
      )
    }
  }
}

/**
 * Button for purchasing an item
 */
@Composable
private fun ItemShopButton(
  modifier: Modifier,
  painterResource: Painter,
  item: String,
  price: Int,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Item icon
      Icon(
        modifier = Modifier.size(60.dp),
        painter = painterResource,
        contentDescription = null,
        tint = Color.Unspecified
      )

      // Title and price information
      Column(
        modifier = Modifier
          .padding(horizontal = 5.dp)
          .weight(1f)
      ) {
        // Item title
        Text(
          text = item,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSecondary
        )

        // Item price
        Text(
          text = "$price Punkte",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }
}

/**
 * Displays the amount of an item in the basket.
 * It includes buttons for increasing and decreasing the amount.
 */
@Composable
private fun ItemAmount(
  itemAmount: Int,
  onIncreaseAmount: () -> Unit,
  onReduceAmount: () -> Unit
) {
  Row(
    modifier = Modifier.padding(start = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Decrement amount
    IconButton(
      onClick = onReduceAmount,
      modifier = Modifier.size(24.dp),
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(R.drawable.minus),
        tint = MaterialTheme.colorScheme.surfaceVariant,
        contentDescription = "subtract"
      )
    }

    // Amount
    Text(
      modifier = Modifier.padding(horizontal = 12.dp),
      text = itemAmount.toString(),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.surfaceVariant
    )

    // Increment amount
    IconButton(
      onClick = onIncreaseAmount,
      modifier = Modifier.size(24.dp),
    ) {
      Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(R.drawable.plus),
        tint = MaterialTheme.colorScheme.surfaceVariant,
        contentDescription = "add"
      )
    }
  }
}