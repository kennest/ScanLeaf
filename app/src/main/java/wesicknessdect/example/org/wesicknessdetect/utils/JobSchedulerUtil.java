package wesicknessdect.example.org.wesicknessdetect.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class JobSchedulerUtil {
    public static void scheduleJob(Context context) {

        ComponentName serviceComponent = new ComponentName(context, OfflineService.class);

        JobInfo jobInbo = new JobInfo.Builder(0, serviceComponent)
                .setMinimumLatency(6 * 1000)      // Temps d'attente minimal avant déclenchement
                .setOverrideDeadline(12 * 1000)// Temps d'attente maximal avant déclenchement
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        jobScheduler.schedule(jobInbo);
        Log.d("JobScheduler Started->", jobScheduler.getAllPendingJobs().size() + "");
    }
}
