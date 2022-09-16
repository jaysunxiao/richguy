package com.richguy.controller;

import com.richguy.resource.HotNoticeResource;
import com.richguy.service.QqBotService;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.zfoo.protocol.util.FileUtils.LS;

/**
 * 群闹铃相关处理逻辑
 *
 * @author godotg
 * @version 3.0
 */
@Component
public class NewsClockController {

    private static final Logger logger = LoggerFactory.getLogger(NewsClockController.class);


    @Autowired
    private QqBotService qqBotService;

    @ResInjection
    private Storage<String, HotNoticeResource> hotNoticeResources;

    public void notice(String message) {
        var no = message + LS + LS +
                StringUtils.format("s.zfoo.net:18888/range?s={}", TimeUtils.dateFormatForDayString(TimeUtils.now())) + LS + LS +
                RandomUtils.randomString(16) + LS + LS +
                RandomUtils.randomEle(new ArrayList<>(hotNoticeResources.getIndex("type", 2))) + LS + LS;
        qqBotService.pushGroupMessage(no);
    }

    public void noticeSimple(String message) {
        qqBotService.pushGroupMessage(message);
    }

    public void noticeThree(String message) {
        notice(message);
        SchedulerBus.schedule(() -> noticeSimple(message), 30 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
        SchedulerBus.schedule(() -> noticeSimple(message), 90 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
    }

    @Scheduler(cron = "0 0 5 ? * MON-FRI")
    public void cron10() {
        noticeSimple("1. 炒股是个体力活，早盘期指，A50期指，富时中国做空指数，50etf沽某月，汇率，中信证券股指期权");
    }

    @Scheduler(cron = "0 0 6 ? * MON-FRI")
    public void cron20() {
        notice("2. 一日之计在于晨，每天早上都要查看：持有股票的板块趋势，公告，董秘，新闻");
    }

    @Scheduler(cron = "0 30 6 ? * MON-FRI")
    public void cron30() {
        noticeSimple("3. 美股相关板块和中概股，中美夜盘期货涨跌情况");
    }

    @Scheduler(cron = "0 0 7 * * ?")
    public void cron40() {
        notice("每天7点，及时关注隔夜全球要闻，消息面");
    }

    @Scheduler(cron = "0 0 8 * * ?")
    public void cron50() {
        noticeSimple("每天8点，投资日历：资本市场大事提醒");
    }

    @Scheduler(cron = "0 30 8 * * ?")
    public void cron60() {
        notice("每天8点30，每日新闻精选");
    }

    @Scheduler(cron = "0 15 9 ? * MON-FRI")
    public void cron70() {
        noticeSimple("开盘啦，战斗了，重点关注：港股竞价，期货资金流入流出");
    }

    @Scheduler(cron = "0 20 9 ? * MON-FRI")
    public void cron80() {
        notice("每天9点20，逆回购信息，请及时关注");
    }

    @Scheduler(cron = "0 50 9 ? * MON-FRI")
    public void cron90() {
        notice("多看盘，少水群，炒低的最佳时间是早上9:30-10:20，下午13:30-14:30，上午机会多，下午一般都是垃圾时间");
    }

    @Scheduler(cron = "0 0 12 ? * MON-FRI")
    public void cron100() {
        notice("每天12点，午评");
    }

    @Scheduler(cron = "0 0 13 ? * MON-FRI")
    public void cron110() {
        noticeSimple("下午开市了，垃圾时间");
    }

    @Scheduler(cron = "0 40 16 ? * MON-FRI")
    public void cron120() {
        noticeSimple("涨停原因|龙虎榜，请兄弟们及时发掘潜在的题材和跟风机会，仔细研究每一个涨停背后的逻辑");
    }

    @Scheduler(cron = "0 0 20 * * ?")
    public void cron130() {
        notice("晚上复盘，看看港资流入板块，东方财富股票热榜，打新股");
    }

    @Scheduler(cron = "0 0 21 * * ?")
    public void cron140() {
        notice("美股盘前要闻一览，不要老盯着涨上去的票，打开想象力看看周边关联板块个股");
    }

    @Scheduler(cron = "0 0 6 * * ?")
    public void cronMorning() {
        var calendar = Calendar.getInstance();
        // 时间，可以为具体的某一时间
        calendar.setTime(new Date());
        var weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        var monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        var yearDay = calendar.get(Calendar.DAY_OF_YEAR);
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay = weekDay - 1;
        }

        if (weekDay == 4) {
            notice("周四60%概率跌，跌的机率比较高，注意观察，稳妥点只保留极低位筹码，周五炒低");
        }

        if (monthDay == 9) {
            noticeThree("每个月9号公布社融数据");
        } else if (monthDay == 15) {
            noticeThree("每个月20号LRP利率公布，月初或者月中MLF公布，请注意主力砸盘意愿，可以适当布局银行证券");
        }
    }


    @Scheduler(cron = "0 0 18 * * ?")
    public void cronAfternoon() {
        var calendar = Calendar.getInstance();
        // 时间，可以为具体的某一时间
        calendar.setTime(new Date());
        var weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        var monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        var yearDay = calendar.get(Calendar.DAY_OF_YEAR);
        if (weekDay == 1) {
            weekDay = 7;
        } else {
            weekDay = weekDay - 1;
        }

        if (weekDay == 7) {
            noticeThree("周末结束了，看看周末的消息面，做一下下周规划吧！");
        }

        if (monthDay == 1) {
            noticeThree("新的一个月开始了，更加谨慎一点吧");
        }
    }

}
