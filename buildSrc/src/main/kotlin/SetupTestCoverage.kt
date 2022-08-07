import kotlinx.kover.tasks.KoverMergedTask
import org.gradle.api.Project

fun Project.setupTestCoverage() {
    tasks.withType(KoverMergedTask::class.java) {
        excludes = listOf(
            "*.BuildConfig",
        )
    }
}
