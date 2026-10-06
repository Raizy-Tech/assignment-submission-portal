package com.example.assignflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignflow.data.model.SubmissionStatus
import com.example.assignflow.ui.theme.StatusLateAmber
import com.example.assignflow.ui.theme.StatusLateBg
import com.example.assignflow.ui.theme.StatusMissingBg
import com.example.assignflow.ui.theme.StatusMissingRed
import com.example.assignflow.ui.theme.StatusPendingBg
import com.example.assignflow.ui.theme.StatusPendingPurple
import com.example.assignflow.ui.theme.StatusSubmittedBg
import com.example.assignflow.ui.theme.StatusSubmittedGreen

@Composable
fun StatusBadge(
    status: SubmissionStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        SubmissionStatus.SUBMITTED -> Triple(
            StatusSubmittedBg,
            StatusSubmittedGreen,
            Icons.Default.CheckCircle
        )
        SubmissionStatus.LATE -> Triple(
            StatusLateBg,
            StatusLateAmber,
            Icons.Default.Warning
        )
        SubmissionStatus.MISSING -> Triple(
            StatusMissingBg,
            StatusMissingRed,
            Icons.Default.Close
        )
        SubmissionStatus.PENDING -> Triple(
            StatusPendingBg,
            StatusPendingPurple,
            Icons.Default.Schedule
        )
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label.uppercase(),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
