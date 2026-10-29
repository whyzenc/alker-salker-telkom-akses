package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {

    private fun buildIcon(name: String, pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black), pathBuilder = pathBuilder)
        }.build()
    }

    val Camera: ImageVector by lazy {
        buildIcon("Camera") {
            moveTo(9.4f, 4f)
            lineTo(8.03f, 5.5f)
            horizontalLineTo(4f)
            curveTo(2.9f, 5.5f, 2f, 6.4f, 2f, 7.5f)
            verticalLineTo(17.5f)
            curveTo(2f, 18.6f, 2.9f, 19.5f, 4f, 19.5f)
            horizontalLineTo(20f)
            curveTo(21.1f, 19.5f, 22f, 18.6f, 22f, 17.5f)
            verticalLineTo(7.5f)
            curveTo(22f, 6.4f, 21.1f, 5.5f, 20f, 5.5f)
            horizontalLineTo(15.97f)
            lineTo(14.6f, 4f)
            horizontalLineTo(9.4f)
            close()
            moveTo(12f, 17.5f)
            curveTo(9.24f, 17.5f, 7f, 15.26f, 7f, 12.5f)
            curveTo(7f, 9.74f, 9.24f, 7.5f, 12f, 7.5f)
            curveTo(14.76f, 7.5f, 17f, 9.74f, 17f, 12.5f)
            curveTo(17f, 15.26f, 14.76f, 17.5f, 12f, 17.5f)
            close()
            moveTo(12f, 9f)
            curveTo(10.07f, 9f, 8.5f, 10.57f, 8.5f, 12.5f)
            curveTo(8.5f, 14.43f, 10.07f, 16f, 12f, 16f)
            curveTo(13.93f, 16f, 15.5f, 14.43f, 15.5f, 12.5f)
            curveTo(15.5f, 10.57f, 13.93f, 9f, 12f, 9f)
            close()
        }
    }

    val Scanner: ImageVector by lazy {
        buildIcon("Scanner") {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
            verticalLineTo(19f)
            curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
            horizontalLineTo(19f)
            curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
            verticalLineTo(5f)
            curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
            close()
            moveTo(19f, 19f)
            horizontalLineTo(5f)
            verticalLineTo(17f)
            horizontalLineTo(19f)
            verticalLineTo(19f)
            close()
            moveTo(19f, 15f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineTo(19f)
            verticalLineTo(15f)
            close()
            moveTo(7f, 7f)
            horizontalLineTo(17f)
            verticalLineTo(9f)
            horizontalLineTo(7f)
            close()
            moveTo(7f, 11f)
            horizontalLineTo(13f)
            verticalLineTo(13f)
            horizontalLineTo(7f)
            close()
        }
    }

    val Hourglass: ImageVector by lazy {
        buildIcon("Hourglass") {
            moveTo(6f, 2f)
            verticalLineTo(8f)
            lineTo(10f, 12f)
            lineTo(6f, 16f)
            verticalLineTo(22f)
            horizontalLineTo(18f)
            verticalLineTo(16f)
            lineTo(14f, 12f)
            lineTo(18f, 8f)
            verticalLineTo(2f)
            horizontalLineTo(6f)
            close()
            moveTo(16f, 16.5f)
            verticalLineTo(20f)
            horizontalLineTo(8f)
            verticalLineTo(16.5f)
            lineTo(12f, 12.5f)
            lineTo(16f, 16.5f)
            close()
        }
    }

    val ErrorCircle: ImageVector by lazy {
        buildIcon("ErrorCircle") {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(13f, 17f)
            horizontalLineTo(11f)
            verticalLineTo(15f)
            horizontalLineTo(13f)
            verticalLineTo(17f)
            close()
            moveTo(13f, 13f)
            horizontalLineTo(11f)
            verticalLineTo(7f)
            horizontalLineTo(13f)
            verticalLineTo(13f)
            close()
        }
    }

    val RadioUnchecked: ImageVector by lazy {
        buildIcon("RadioUnchecked") {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(12f, 20f)
            curveTo(7.58f, 20f, 4f, 16.42f, 4f, 12f)
            curveTo(4f, 7.58f, 7.58f, 4f, 12f, 4f)
            curveTo(16.42f, 4f, 20f, 7.58f, 20f, 12f)
            curveTo(20f, 16.42f, 16.42f, 20f, 12f, 20f)
            close()
        }
    }

    val Swap: ImageVector by lazy {
        buildIcon("Swap") {
            moveTo(6.99f, 11f)
            lineTo(3f, 15f)
            lineTo(6.99f, 19f)
            verticalLineTo(16f)
            horizontalLineTo(14f)
            verticalLineTo(14f)
            horizontalLineTo(6.99f)
            verticalLineTo(11f)
            close()
            moveTo(21f, 9f)
            lineTo(17.01f, 5f)
            verticalLineTo(8f)
            horizontalLineTo(10f)
            verticalLineTo(10f)
            horizontalLineTo(17.01f)
            verticalLineTo(13f)
            lineTo(21f, 9f)
            close()
        }
    }

    val Logout: ImageVector by lazy {
        buildIcon("Logout") {
            moveTo(17f, 7f)
            lineTo(15.59f, 8.41f)
            lineTo(17.17f, 10f)
            horizontalLineTo(9f)
            verticalLineTo(12f)
            horizontalLineTo(17.17f)
            lineTo(15.59f, 13.59f)
            lineTo(17f, 15f)
            lineTo(21f, 11f)
            lineTo(17f, 7f)
            close()
            moveTo(4f, 5f)
            horizontalLineTo(12f)
            verticalLineTo(3f)
            horizontalLineTo(4f)
            curveTo(2.9f, 3f, 2f, 3.9f, 2f, 5f)
            verticalLineTo(19f)
            curveTo(2f, 20.1f, 2.9f, 21f, 4f, 21f)
            horizontalLineTo(12f)
            verticalLineTo(19f)
            horizontalLineTo(4f)
            verticalLineTo(5f)
            close()
        }
    }

    val Analytics: ImageVector by lazy {
        buildIcon("Analytics") {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
            verticalLineTo(19f)
            curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
            horizontalLineTo(19f)
            curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
            verticalLineTo(5f)
            curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
            close()
            moveTo(9f, 17f)
            horizontalLineTo(7f)
            verticalLineTo(10f)
            horizontalLineTo(9f)
            verticalLineTo(17f)
            close()
            moveTo(13f, 17f)
            horizontalLineTo(11f)
            verticalLineTo(7f)
            horizontalLineTo(13f)
            verticalLineTo(17f)
            close()
            moveTo(17f, 17f)
            horizontalLineTo(15f)
            verticalLineTo(13f)
            horizontalLineTo(17f)
            verticalLineTo(17f)
            close()
        }
    }

    val Download: ImageVector by lazy {
        buildIcon("Download") {
            moveTo(19.35f, 10.04f)
            curveTo(18.67f, 6.59f, 15.64f, 4f, 12f, 4f)
            curveTo(9.11f, 4f, 6.6f, 5.64f, 5.35f, 8.04f)
            curveTo(2.34f, 8.36f, 0f, 10.91f, 0f, 14f)
            curveTo(0f, 17.31f, 2.69f, 20f, 6f, 20f)
            horizontalLineTo(19f)
            curveTo(21.76f, 20f, 24f, 17.76f, 24f, 15f)
            curveTo(24f, 12.36f, 21.95f, 10.22f, 19.35f, 10.04f)
            close()
            moveTo(17f, 13f)
            lineTo(12f, 18f)
            lineTo(7f, 13f)
            horizontalLineTo(10f)
            verticalLineTo(9f)
            horizontalLineTo(14f)
            verticalLineTo(13f)
            horizontalLineTo(17f)
            close()
        }
    }

    val People: ImageVector by lazy {
        buildIcon("People") {
            moveTo(16f, 11f)
            curveTo(17.66f, 11f, 18.99f, 9.66f, 18.99f, 8f)
            curveTo(18.99f, 6.34f, 17.66f, 5f, 16f, 5f)
            curveTo(14.34f, 5f, 13f, 6.34f, 13f, 8f)
            curveTo(13f, 9.66f, 14.34f, 11f, 16f, 11f)
            close()
            moveTo(8f, 11f)
            curveTo(9.66f, 11f, 11f, 9.66f, 11f, 8f)
            curveTo(11f, 6.34f, 9.66f, 5f, 8f, 5f)
            curveTo(6.34f, 5f, 5f, 6.34f, 5f, 8f)
            curveTo(5f, 9.66f, 6.34f, 11f, 8f, 11f)
            close()
            moveTo(8f, 13f)
            curveTo(5.67f, 13f, 1f, 14.17f, 1f, 16.5f)
            verticalLineTo(19f)
            horizontalLineTo(15f)
            verticalLineTo(16.5f)
            curveTo(15f, 14.17f, 10.33f, 13f, 8f, 13f)
            close()
            moveTo(16f, 13f)
            curveTo(15.71f, 13f, 15.38f, 13.02f, 15.03f, 13.05f)
            curveTo(16.19f, 13.89f, 17f, 15.02f, 17f, 16.5f)
            verticalLineTo(19f)
            horizontalLineTo(23f)
            verticalLineTo(16.5f)
            curveTo(23f, 14.17f, 18.33f, 13f, 16f, 13f)
            close()
        }
    }

    val History: ImageVector by lazy {
        buildIcon("History") {
            moveTo(13f, 3f)
            curveTo(8.03f, 3f, 4f, 7.03f, 4f, 12f)
            horizontalLineTo(1f)
            lineTo(4.89f, 15.89f)
            lineTo(5f, 16f)
            lineTo(9f, 12f)
            horizontalLineTo(6f)
            curveTo(6f, 8.13f, 9.13f, 5f, 13f, 5f)
            curveTo(16.87f, 5f, 20f, 8.13f, 20f, 12f)
            curveTo(20f, 15.87f, 16.87f, 19f, 13f, 19f)
            curveTo(11.07f, 19f, 9.32f, 18.21f, 8.06f, 16.94f)
            lineTo(6.64f, 18.36f)
            curveTo(8.27f, 20f, 10.51f, 21f, 13f, 21f)
            curveTo(17.97f, 21f, 22f, 16.97f, 22f, 12f)
            curveTo(22f, 7.03f, 17.97f, 3f, 13f, 3f)
            close()
            moveTo(12f, 8f)
            verticalLineTo(13f)
            lineTo(16.28f, 15.54f)
            lineTo(17f, 14.33f)
            lineTo(13.5f, 12.25f)
            verticalLineTo(8f)
            horizontalLineTo(12f)
            close()
        }
    }

    val ZoomIn: ImageVector by lazy {
        buildIcon("ZoomIn") {
            moveTo(15.5f, 14f)
            horizontalLineTo(14.71f)
            lineTo(14.43f, 13.73f)
            curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
            curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
            curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f)
            curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
            curveTo(11.11f, 16f, 12.59f, 15.41f, 13.73f, 14.43f)
            lineTo(14f, 14.71f)
            verticalLineTo(15.5f)
            lineTo(19f, 20.49f)
            lineTo(20.49f, 19f)
            lineTo(15.5f, 14f)
            close()
            moveTo(9.5f, 14f)
            curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
            curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f)
            curveTo(11.99f, 5f, 14f, 7.01f, 14f, 9.5f)
            curveTo(14f, 11.99f, 11.99f, 14f, 9.5f, 14f)
            close()
            moveTo(10f, 7f)
            horizontalLineTo(9f)
            verticalLineTo(9f)
            horizontalLineTo(7f)
            verticalLineTo(10f)
            horizontalLineTo(9f)
            verticalLineTo(12f)
            horizontalLineTo(10f)
            verticalLineTo(10f)
            horizontalLineTo(12f)
            verticalLineTo(9f)
            horizontalLineTo(10f)
            verticalLineTo(7f)
            close()
        }
    }

    val PhotoLibrary: ImageVector by lazy {
        buildIcon("PhotoLibrary") {
            moveTo(22f, 16f)
            verticalLineTo(4f)
            curveTo(22f, 2.9f, 21.1f, 2f, 20f, 2f)
            horizontalLineTo(8f)
            curveTo(6.9f, 2f, 6f, 2.9f, 6f, 4f)
            verticalLineTo(16f)
            curveTo(6f, 17.1f, 6.9f, 18f, 8f, 18f)
            horizontalLineTo(20f)
            curveTo(21.1f, 18f, 22f, 17.1f, 22f, 16f)
            close()
            moveTo(11f, 12f)
            lineTo(13.03f, 14.71f)
            lineTo(16f, 11f)
            lineTo(20f, 16f)
            horizontalLineTo(8f)
            lineTo(11f, 12f)
            close()
            moveTo(2f, 6f)
            verticalLineTo(20f)
            curveTo(2f, 21.1f, 2.9f, 22f, 4f, 22f)
            horizontalLineTo(18f)
            verticalLineTo(20f)
            horizontalLineTo(4f)
            verticalLineTo(6f)
            horizontalLineTo(2f)
            close()
        }
    }

    val AdminShield: ImageVector by lazy {
        buildIcon("AdminShield") {
            moveTo(12f, 1f)
            lineTo(3f, 5f)
            verticalLineTo(11f)
            curveTo(3f, 16.55f, 6.84f, 21.74f, 12f, 23f)
            curveTo(17.16f, 21.74f, 21f, 16.55f, 21f, 11f)
            verticalLineTo(5f)
            lineTo(12f, 1f)
            close()
            moveTo(12f, 11.99f)
            horizontalLineTo(19f)
            curveTo(18.47f, 16.11f, 15.72f, 19.78f, 12f, 20.93f)
            verticalLineTo(12f)
            horizontalLineTo(5f)
            verticalLineTo(6.3f)
            lineTo(12f, 3.19f)
            verticalLineTo(11.99f)
            close()
        }
    }

    val Engineering: ImageVector by lazy {
        buildIcon("Engineering") {
            moveTo(12f, 15f)
            curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f)
            curveTo(15f, 10.34f, 13.66f, 9f, 12f, 9f)
            curveTo(10.34f, 9f, 9f, 10.34f, 9f, 12f)
            curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
            close()
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(12f, 20f)
            curveTo(7.59f, 20f, 4f, 16.41f, 4f, 12f)
            curveTo(4f, 7.59f, 7.59f, 4f, 12f, 4f)
            curveTo(16.41f, 4f, 20f, 7.59f, 20f, 12f)
            curveTo(20f, 16.41f, 16.41f, 20f, 12f, 20f)
            close()
        }
    }

    val Visibility: ImageVector by lazy {
        buildIcon("Visibility") {
            moveTo(12f, 4.5f)
            curveTo(7f, 4.5f, 2.73f, 7.61f, 1f, 12f)
            curveTo(2.73f, 16.39f, 7f, 19.5f, 12f, 19.5f)
            curveTo(17f, 19.5f, 21.27f, 16.39f, 23f, 12f)
            curveTo(21.27f, 7.61f, 17f, 4.5f, 12f, 4.5f)
            close()
            moveTo(12f, 17f)
            curveTo(9.24f, 17f, 7f, 14.76f, 7f, 12f)
            curveTo(7f, 9.24f, 9.24f, 7f, 12f, 7f)
            curveTo(14.76f, 7f, 17f, 9.24f, 17f, 12f)
            curveTo(17f, 14.76f, 14.76f, 17f, 12f, 17f)
            close()
            moveTo(12f, 9f)
            curveTo(10.34f, 9f, 9f, 10.34f, 9f, 12f)
            curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f)
            curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f)
            curveTo(15f, 10.34f, 13.66f, 9f, 12f, 9f)
            close()
        }
    }

    val VisibilityOff: ImageVector by lazy {
        buildIcon("VisibilityOff") {
            moveTo(12f, 7f)
            curveTo(13.66f, 7f, 15f, 8.34f, 15f, 10f)
            curveTo(15f, 10.64f, 14.8f, 11.23f, 14.46f, 11.72f)
            lineTo(16.53f, 13.79f)
            curveTo(18.27f, 12.35f, 19.61f, 10.32f, 20.31f, 8f)
            curveTo(18.61f, 4.11f, 14.74f, 1.5f, 10.22f, 1.5f)
            curveTo(8.74f, 1.5f, 7.34f, 1.83f, 6.07f, 2.42f)
            lineTo(8.08f, 4.43f)
            curveTo(8.75f, 4.16f, 9.47f, 4f, 10.22f, 4f)
            curveTo(11.88f, 4f, 13.22f, 5.34f, 13.22f, 7f)
            close()
            moveTo(2.71f, 3.16f)
            lineTo(1.44f, 4.43f)
            lineTo(4.49f, 7.48f)
            curveTo(3.29f, 8.78f, 2.37f, 10.3f, 1.82f, 12f)
            curveTo(3.52f, 15.89f, 7.39f, 18.5f, 11.91f, 18.5f)
            curveTo(13.39f, 18.5f, 14.79f, 18.17f, 16.06f, 17.58f)
            lineTo(19.57f, 21.09f)
            lineTo(20.84f, 19.82f)
            lineTo(2.71f, 3.16f)
            close()
        }
    }

    val Compress: ImageVector by lazy {
        buildIcon("Compress") {
            moveTo(8f, 19f)
            horizontalLineTo(11f)
            verticalLineTo(22f)
            horizontalLineTo(13f)
            verticalLineTo(19f)
            horizontalLineTo(16f)
            lineTo(12f, 15f)
            lineTo(8f, 19f)
            close()
            moveTo(16f, 5f)
            horizontalLineTo(13f)
            verticalLineTo(2f)
            horizontalLineTo(11f)
            verticalLineTo(5f)
            horizontalLineTo(8f)
            lineTo(12f, 9f)
            lineTo(16f, 5f)
            close()
            moveTo(4f, 11f)
            horizontalLineTo(20f)
            verticalLineTo(13f)
            horizontalLineTo(4f)
            verticalLineTo(11f)
            close()
        }
    }

    val AssignmentCheck: ImageVector by lazy {
        buildIcon("AssignmentCheck") {
            moveTo(19f, 3f)
            horizontalLineTo(14.82f)
            curveTo(14.4f, 1.84f, 13.3f, 1f, 12f, 1f)
            curveTo(10.7f, 1f, 9.6f, 1.84f, 9.18f, 3f)
            horizontalLineTo(5f)
            curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
            verticalLineTo(19f)
            curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
            horizontalLineTo(19f)
            curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
            verticalLineTo(5f)
            curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
            close()
            moveTo(10f, 17f)
            lineTo(6f, 13f)
            lineTo(7.41f, 11.59f)
            lineTo(10f, 14.17f)
            lineTo(16.59f, 7.58f)
            lineTo(18f, 9f)
            lineTo(10f, 17f)
            close()
        }
    }
}
