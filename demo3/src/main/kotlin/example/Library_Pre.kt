import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

data class Activity(
    val name: String,
    val url: String
)

@Composable
fun ActivitiesCard(activities: List<Activity>) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "近期活动",
                )

                Spacer(modifier = Modifier.height(8.dp))

                activities.forEach { activity ->
                    Text(
                        text = activity.name,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                uriHandler.openUri(activity.url)
                            }
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun ActivitiesScreen() {
    val activities = listOf(
        Activity("活动1", "https://example.com/1"),
        Activity("活动2", "https://example.com/2"),
        Activity("活动3", "https://example.com/3")
    )

    ActivitiesCard(activities)
}

