package ru.dzmiter.jobs

import grails.util.Holders
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

/**
 * @author dzmiter
 */
class HuntJob implements Job {

    static mapWith = "none"
    private static final Map<String, String> users =
        ['95752056': '568a7d0ada86d4d9a8b12424f70e1bdb',
                '29326730': 'f57592dce817831b51474535071dcdc4']

    @Override
    void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        def hunterService = Holders.applicationContext.getBean("hunterService")
        users.each { id, auth_key ->
            hunterService.hunt(id, auth_key);
        }
    }

}
