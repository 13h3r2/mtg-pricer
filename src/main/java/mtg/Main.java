package mtg;

import mtg.api.ExportServlet;
import mtg.scheduler.UpdatePriceJob;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class Main {

    public static void main(String[] args) throws IOException, SchedulerException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("mtg").setLevel(Level.DEBUG);
        LoggerFactory.getLogger("mtg").info("Go!");

        initGrizzly();
        initScheduler();

    }

    private static void initGrizzly() throws IOException {
        GrizzlyWebServer ws = new GrizzlyWebServer(19998, "src/main/webapp");
        // Jersey web resources
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.setContextPath("/api");
        jerseyAdapter.addInitParameter("com.sun.jersey.config.property.packages", "mtg.api");
        jerseyAdapter.setServletInstance(new ServletContainer());

        ws.addGrizzlyAdapter(jerseyAdapter, new String[]{"/api"});
        GrizzlyAdapter staticAdapter = new GrizzlyAdapter("src/main/webapp") {
            @Override
            public void service(GrizzlyRequest request, GrizzlyResponse response) throws Exception {

            }
        };
        staticAdapter.setHandleStaticResources(true);
        ws.addGrizzlyAdapter(staticAdapter, new String[]{"/"});

        ServletAdapter exportAdapter = new ServletAdapter();
        exportAdapter.setServletPath("/export");
        exportAdapter.setServletInstance(new ExportServlet());
        ws.addGrizzlyAdapter(exportAdapter, new String[]{"/export"});
        ws.start();
    }

    private static void initScheduler() throws SchedulerException {
        Calendar startDate = new GregorianCalendar();
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        System.out.println(startDate.getTime());

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder
                .newJob(UpdatePriceJob.class)
                .build();

        CalendarIntervalScheduleBuilder schedule = CalendarIntervalScheduleBuilder
                .calendarIntervalSchedule()
                .withIntervalInDays(1)
                .withMisfireHandlingInstructionDoNothing();

        CalendarIntervalTrigger trigger = TriggerBuilder
                .newTrigger()
                .startAt(startDate.getTime())
                .withSchedule(schedule)
                .build();

        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }
}