package com.richguy.controller;

import com.richguy.event.ServerStartEvent;
import com.richguy.resource.HotNoticeResource;
import com.richguy.service.QqBotService;
import com.zfoo.event.model.anno.EventReceiver;
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
    private Storage<Integer, HotNoticeResource> hotNoticeResources;

    public void notice(String message) {
        var no = message + LS + LS +
                StringUtils.format("s.zfoo.net:18888/{}A{}", TimeUtils.dateFormatForDayString(TimeUtils.now()), RandomUtils.randomString(16));
//        var no = message + LS + LS +
//                StringUtils.format("s.zfoo.net:18888/{}A{}", TimeUtils.dateFormatForDayString(TimeUtils.now()), RandomUtils.randomString(16)) + LS + LS +
//                RandomUtils.randomEle(new ArrayList<>(hotNoticeResources.getIndex("type", 2)));
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

    @EventReceiver
    public void onServerStartEvent(ServerStartEvent event) {
        SchedulerBus.schedule(() -> notice("服务器重新启动"), 30 * TimeUtils.MILLIS_PER_SECOND, TimeUnit.MILLISECONDS);
    }

    @Scheduler(cron = "0 0 2 * * ?")
    public void cron0() {
        noticeSimple("大A的全部智慧就包含在这五个字里面：等待和希望！" + LS + RandomUtils.randomEle(hotNoticeResources.getIndex("type", 9)));
    }

    @Scheduler(cron = "0 0 3 * * ?")
    public void cron1() {
        noticeSimple(RandomUtils.randomEle(hotNoticeResources.getIndex("type", 9)).toString());
    }

    @Scheduler(cron = "0 0 5 ? * MON-FRI")
    public void cron10() {
        notice("1. 炒股是个体力活，早盘期指，A50期指，富时中国做空指数，50etf沽某月，汇率，中信证券股指期权" + LS +
                "2. 一日之计在于晨，每天早上都要查看：持有股票的板块趋势，公告，董秘，新闻" + LS +
                "3. 美股相关板块和中概股，中美夜盘期货涨跌情况");
    }


    @Scheduler(cron = "0 0 7 * * ?")
    public void cron40() {
        notice("每天7点，及时关注隔夜全球要闻，消息面");
    }

    @Scheduler(cron = "0 0 8 * * ?")
    public void cron50() {
        notice("每天8点，投资日历：资本市场大事提醒");
    }

    @Scheduler(cron = "0 30 8 * * ?")
    public void cron60() {
        notice("每天8点30，每日新闻精选");
    }

    @Scheduler(cron = "0 15 9 ? * MON-FRI")
    public void cron70() {
        notice("开盘啦，战斗了，重点关注：微信群投顾消息和公众号消息面，港股竞价，期货资金流入流出");
    }

    @Scheduler(cron = "0 22 9 ? * MON-FRI")
    public void cron80() {
        notice("每天9点20，逆回购信息，请及时关注");
    }

    @Scheduler(cron = "0 50 9 ? * MON-FRI")
    public void cron90() {
        notice("多看盘，少水群，炒低的最佳时间是早上9:30-10:20，下午13:30-14:30，上午机会多，下午一般都是垃圾时间");
    }

    @Scheduler(cron = "0 30 10 ? * MON-FRI")
    public void cron91() {
        notice("价投战法，自己熟悉重点收藏的票反复套利，注意分析曲线，量能和macd，在一个票上反复套都比你乱操作赚的多");
    }

    @Scheduler(cron = "0 0 12 * * ?")
    public void cron100() {
        notice("每天12点，午评，午间新闻");
    }

    @Scheduler(cron = "0 0 13 ? * MON-FRI")
    public void cron110() {
        notice("下午开市了，下午一般是垃圾时间，注意一下新题材" + LS + LS +
                "新概念战法，靠着爬虫爬爬新概念，其它的啥都不做，只做爬虫爬的新概念，就能稳定赚");
    }

    @Scheduler(cron = "0 30 13 ? * MON-FRI")
    public void cron111() {
        notice("青叔跟风战法的选股时间到了，在指数、题材良好，买入主流题材、放量分歧龙头股是最优的操作。");
    }

    @Scheduler(cron = "0 0 14 ? * MON-FRI")
    public void cron112() {
        notice("青叔跟风战法的选股时间到了，在指数、题材退潮，买入次题材、缩量拉升(封板)跟风股是亏钱最快的操作。");
    }

    @Scheduler(cron = "0 30 14 ? * MON-FRI")
    public void cron113() {
        notice("提前埋伏战法，预测推理，预测未来趋势踩节奏，板块的参考历史趋势，消息推演，技术曲线分析，寻找将要发力的板块和妖股" + LS + LS +
                "尾盘注意欧美股指期货，和人民币汇率是否有变化");
    }

    @Scheduler(cron = "0 0 15 ? * MON-FRI")
    public void cron114() {
        notice("止跌了，兄弟们，明天勒紧裤腰带继续战斗");
    }

    @Scheduler(cron = "0 40 16 ? * MON-FRI")
    public void cron120() {
        notice("龙虎榜出来了，注意涨停原因，请兄弟们及时发掘潜在的题材和跟风机会，仔细研究每一个涨停背后的逻辑");
    }

    @Scheduler(cron = "0 0 19 ? * MON-FRI")
    public void cron130() {
        notice("晚上复盘，看看港资流入板块，东方财富股票热榜，打新股");
    }

    @Scheduler(cron = "0 30 20 * * ?")
    public void cron131() {
        notice("新闻联播");
    }

    @Scheduler(cron = "0 0 22 ? * MON-FRI")
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
        } else if (monthDay == 1 || monthDay == 15 || monthDay == 28) {
            noticeThree("每个月20号LRP利率公布，月初或者月中MLF公布，请注意主力砸盘意愿，可以适当布局银行证券");
        }

        if (weekDay == 1 || weekDay == 4 || weekDay == 7) {
            notice("1.31 所有板块的年报预告" + LS +
                    "2.28 科创版业绩快报或正式年报" + LS +
                    "4.15 深圳主板，中小板一季报预告" + LS +
                    "4.30 所有板块的年报和一季报" + LS +
                    "5，6月，业绩真空期" + LS +
                    "7.15 深圳主板，中小板中报预告" + LS +
                    "8.30 所有板块中报" + LS +
                    "10.15 深圳主板，中小板三季报预告" + LS +
                    "10.30 所有板块三季报" + LS +
                    "11，12月，业绩真空期");
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
            noticeThree("多学习一下不同的交易策略，期权交易，https://www.bilibili.com/video/BV1d44y127DL");
        }

        if (monthDay == 1) {
            noticeThree("新的一个月开始了，更加耐心和谨慎一点吧");
        }
    }

}
