import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import ru.dzmiter.jobs.BonusJob
import ru.dzmiter.jobs.HuntJob

class BootStrap {
    def init = { servletContext ->
        JobDetail hunt = JobBuilder.newJob(HuntJob.class)
                .withIdentity("hunt", Scheduler.DEFAULT_GROUP).build();
        JobDetail bonus = JobBuilder.newJob(BonusJob.class)
                .withIdentity("bonus", Scheduler.DEFAULT_GROUP).build();

        Trigger huntTrigger = TriggerBuilder.newTrigger()
                .withIdentity('huntTrigger', Scheduler.DEFAULT_GROUP)
                .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(500).repeatForever())
                .build();
        Trigger bonusTrigger = TriggerBuilder.newTrigger()
                .withIdentity('bonusTrigger', Scheduler.DEFAULT_GROUP)
                .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(12).repeatForever())
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        scheduler.scheduleJob(hunt, huntTrigger);
        scheduler.scheduleJob(bonus, bonusTrigger);
    }
    def destroy = {}
}
