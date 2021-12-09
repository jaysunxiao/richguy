package com.richguy.industry;

import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class IndustryParseTest {


    @Test
    public void test() throws ParserConfigurationException, IOException, SAXException {
        var html = "<div class=\"cate_inner visible\">\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">A~E</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/301558/\" target=\"_blank\">阿里巴巴概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300800/\" target=\"_blank\">安防</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301496/\" target=\"_blank\">白酒概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308594/\" target=\"_blank\">标普道琼斯A股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301259/\" target=\"_blank\">百度概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308743/\" target=\"_blank\">北交所概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307408/\" target=\"_blank\">边缘计算</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/305376/\" target=\"_blank\">丙烯酸</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307550/\" target=\"_blank\">冰雪产业</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300018/\" target=\"_blank\">参股保险</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/306912/\" target=\"_blank\">宁德时代概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300100/\" target=\"_blank\">参股券商</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300389/\" target=\"_blank\">参股新三板</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300157/\" target=\"_blank\">草甘膦</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301270/\" target=\"_blank\">参股银行</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308432/\" target=\"_blank\">长三角一体化</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300926/\" target=\"_blank\">超级电容</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/306750/\" target=\"_blank\">超级品牌</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308501/\" target=\"_blank\">超级真菌</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308473/\" target=\"_blank\">超清视频</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300163/\" target=\"_blank\">车联网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300786/\" target=\"_blank\">充电桩</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308753/\" target=\"_blank\">抽水蓄能</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300168/\" target=\"_blank\">创投</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308568/\" target=\"_blank\">创业板重组松绑</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/306380/\" target=\"_blank\">储能</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301123/\" target=\"_blank\">大豆</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300013/\" target=\"_blank\">大飞机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308696/\" target=\"_blank\">代糖概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307816/\" target=\"_blank\">国家大基金持股 </a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300835/\" target=\"_blank\">大数据</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300733/\" target=\"_blank\">锂电池</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302027/\" target=\"_blank\">电子竞技</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300186/\" target=\"_blank\">电子商务</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308700/\" target=\"_blank\">第三代半导体</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301623/\" target=\"_blank\">地下管网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308477/\" target=\"_blank\">电力物联网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307822/\" target=\"_blank\">动力电池回收</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308537/\" target=\"_blank\">动物疫苗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308002/\" target=\"_blank\">独角兽概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308803/\" target=\"_blank\">EDR概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300891/\" target=\"_blank\">三胎概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308555/\" target=\"_blank\">ETC</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">F~J</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/308572/\" target=\"_blank\">仿制药一致性评价</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308590/\" target=\"_blank\">分拆上市意愿</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300200/\" target=\"_blank\">风电</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308438/\" target=\"_blank\">芬太尼</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300085/\" target=\"_blank\">氟化工概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301236/\" target=\"_blank\">福建自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308648/\" target=\"_blank\">富媒体</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307954/\" target=\"_blank\">富士康概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308708/\" target=\"_blank\">辅助生殖</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300890/\" target=\"_blank\">高端装备</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301518/\" target=\"_blank\">高铁</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308732/\" target=\"_blank\">共同富裕示范区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301620/\" target=\"_blank\">工业4.0</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301477/\" target=\"_blank\">工业大麻</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301780/\" target=\"_blank\">工业互联网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300941/\" target=\"_blank\">工业母机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301284/\" target=\"_blank\">供应链金融</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302154/\" target=\"_blank\">股权转让</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301630/\" target=\"_blank\">广东自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301079/\" target=\"_blank\">光伏概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308582/\" target=\"_blank\">光刻胶</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300221/\" target=\"_blank\">固废处理</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308531/\" target=\"_blank\">国产操作系统</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301524/\" target=\"_blank\">国产软件</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300082/\" target=\"_blank\">军工</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302174/\" target=\"_blank\">钴</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302166/\" target=\"_blank\">共享单车</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300105/\" target=\"_blank\">海工装备</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308428/\" target=\"_blank\">海南自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300066/\" target=\"_blank\">海峡两岸</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301470/\" target=\"_blank\">航空发动机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300236/\" target=\"_blank\">国产航母</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300973/\" target=\"_blank\">航运概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301783/\" target=\"_blank\">杭州亚运会</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300238/\" target=\"_blank\">核电</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308547/\" target=\"_blank\">黑龙江自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300087/\" target=\"_blank\">横琴新区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308719/\" target=\"_blank\">新冠检测</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308698/\" target=\"_blank\">核准制次新股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301249/\" target=\"_blank\">互联网彩票</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301209/\" target=\"_blank\">互联网金融</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300444/\" target=\"_blank\">互联网医疗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308731/\" target=\"_blank\">鸿蒙概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308775/\" target=\"_blank\">换电概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300248/\" target=\"_blank\">黄金概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308202/\" target=\"_blank\">环氧丙烷</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301459/\" target=\"_blank\">华为概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308527/\" target=\"_blank\">华为海思概念股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308507/\" target=\"_blank\">华为汽车</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301490/\" target=\"_blank\">沪股通</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301628/\" target=\"_blank\">互联网保险</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301522/\" target=\"_blank\">集成电路概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300814/\" target=\"_blank\">家用电器</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300730/\" target=\"_blank\">节能环保</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300053/\" target=\"_blank\">节能照明</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300265/\" target=\"_blank\">金改</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300061/\" target=\"_blank\">京津冀一体化</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308366/\" target=\"_blank\">今日头条概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300816/\" target=\"_blank\">机器人概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301146/\" target=\"_blank\">基因测序</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301799/\" target=\"_blank\">健康中国</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301786/\" target=\"_blank\">军民融合</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300269/\" target=\"_blank\">举牌</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">K~O</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/308699/\" target=\"_blank\">科创次新股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301117/\" target=\"_blank\">可降解塑料</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300277/\" target=\"_blank\">可燃冰</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301610/\" target=\"_blank\">壳资源</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301414/\" target=\"_blank\">口罩</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308659/\" target=\"_blank\">快手概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301564/\" target=\"_blank\">跨境电商</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308566/\" target=\"_blank\">垃圾分类</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301179/\" target=\"_blank\">冷链物流</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300830/\" target=\"_blank\">量子科技</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300098/\" target=\"_blank\">磷化工</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300038/\" target=\"_blank\">流感</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308760/\" target=\"_blank\">绿色电力</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300923/\" target=\"_blank\">生态农业</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302150/\" target=\"_blank\">蚂蚁金服概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300084/\" target=\"_blank\">煤化工</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308716/\" target=\"_blank\">煤炭概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308657/\" target=\"_blank\">免税店</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308628/\" target=\"_blank\">MiniLED</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301346/\" target=\"_blank\">民营医院</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/303944/\" target=\"_blank\">MSCI概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301096/\" target=\"_blank\">钠离子电池</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308748/\" target=\"_blank\">NFT概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301632/\" target=\"_blank\">农村电商</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301306/\" target=\"_blank\">农机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308016/\" target=\"_blank\">农业种植</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301154/\" target=\"_blank\">OLED</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">P~T</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/308774/\" target=\"_blank\">培育钻石</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301497/\" target=\"_blank\">啤酒概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308709/\" target=\"_blank\">拼多多概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300309/\" target=\"_blank\">苹果概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300134/\" target=\"_blank\">PM2.5</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301624/\" target=\"_blank\">PPP概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308694/\" target=\"_blank\">汽车拆解概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301471/\" target=\"_blank\">汽车电子</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308725/\" target=\"_blank\">汽车芯片</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300220/\" target=\"_blank\">期货概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301794/\" target=\"_blank\">青蒿素</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300075/\" target=\"_blank\">禽流感</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302045/\" target=\"_blank\">区块链</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301292/\" target=\"_blank\">染料</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300316/\" target=\"_blank\">燃料电池</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/305790/\" target=\"_blank\">燃料乙醇</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302035/\" target=\"_blank\">人工智能</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308517/\" target=\"_blank\">人造肉</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301166/\" target=\"_blank\">人脸识别</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300900/\" target=\"_blank\">融资融券</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308467/\" target=\"_blank\">柔性屏</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300983/\" target=\"_blank\">乳业</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301602/\" target=\"_blank\">赛马概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301402/\" target=\"_blank\">上海国资改革</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301128/\" target=\"_blank\">上海自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301636/\" target=\"_blank\">深股通</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300106/\" target=\"_blank\">生物疫苗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300769/\" target=\"_blank\">生物医药</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308710/\" target=\"_blank\">社区团购</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307512/\" target=\"_blank\">石墨电极</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300337/\" target=\"_blank\">石墨烯</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300023/\" target=\"_blank\">食品安全</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308661/\" target=\"_blank\">室外经济</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301531/\" target=\"_blank\">首发新股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301050/\" target=\"_blank\">手机游戏</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300345/\" target=\"_blank\">水利</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307826/\" target=\"_blank\">水泥概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308801/\" target=\"_blank\">数据安全</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308642/\" target=\"_blank\">数据中心</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301997/\" target=\"_blank\">数字货币</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308479/\" target=\"_blank\">数字孪生</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308525/\" target=\"_blank\">数字乡村</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308296/\" target=\"_blank\">送转填权</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300862/\" target=\"_blank\">ST板块</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301171/\" target=\"_blank\">深圳国资改革</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300351/\" target=\"_blank\">钛白粉概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308620/\" target=\"_blank\">胎压监测</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300352/\" target=\"_blank\">碳纤维</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308728/\" target=\"_blank\">碳中和</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300845/\" target=\"_blank\">特钢概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300353/\" target=\"_blank\">特高压</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302010/\" target=\"_blank\">腾讯概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302674/\" target=\"_blank\">特色小镇</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301121/\" target=\"_blank\">特斯拉</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301229/\" target=\"_blank\">天津自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300358/\" target=\"_blank\">天然气</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301605/\" target=\"_blank\">体育产业</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308718/\" target=\"_blank\">同花顺漂亮100</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300362/\" target=\"_blank\">通用航空</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300858/\" target=\"_blank\">土地流转</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">U~Z</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/302059/\" target=\"_blank\">网络直播</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308630/\" target=\"_blank\">网红经济</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300019/\" target=\"_blank\">网络游戏</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302142/\" target=\"_blank\">网约车</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308300/\" target=\"_blank\">MCU芯片</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300722/\" target=\"_blank\">卫星导航</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300806/\" target=\"_blank\">文化传媒</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308791/\" target=\"_blank\">WiFi 6</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300378/\" target=\"_blank\">物联网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300889/\" target=\"_blank\">无人机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301286/\" target=\"_blank\">无人驾驶</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/304756/\" target=\"_blank\">无人零售</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300374/\" target=\"_blank\">污水处理</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300723/\" target=\"_blank\">无线充电</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308612/\" target=\"_blank\">无线耳机</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308717/\" target=\"_blank\">物业管理</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302170/\" target=\"_blank\">微信小程序</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302182/\" target=\"_blank\">雄安新区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300836/\" target=\"_blank\">乡村振兴</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308636/\" target=\"_blank\">消毒剂</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308384/\" target=\"_blank\">消费电子概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300809/\" target=\"_blank\">小金属概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301278/\" target=\"_blank\">小米概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301543/\" target=\"_blank\">细胞免疫治疗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300883/\" target=\"_blank\">新材料概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300870/\" target=\"_blank\">新股与次新股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300386/\" target=\"_blank\">新疆振兴</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302436/\" target=\"_blank\">新零售</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300008/\" target=\"_blank\">新能源汽车</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301085/\" target=\"_blank\">芯片概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308650/\" target=\"_blank\">新三板精选层概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301377/\" target=\"_blank\">信托概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300756/\" target=\"_blank\">网络安全</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301362/\" target=\"_blank\">新型烟草</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300777/\" target=\"_blank\">稀缺资源</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300382/\" target=\"_blank\">稀土永磁</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301699/\" target=\"_blank\">虚拟现实</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308733/\" target=\"_blank\">牙科医疗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308560/\" target=\"_blank\">烟草</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301576/\" target=\"_blank\">养鸡</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301494/\" target=\"_blank\">养老概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301546/\" target=\"_blank\">央企国资改革</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/307904/\" target=\"_blank\">盐湖提锂</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301252/\" target=\"_blank\">眼科医疗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300402/\" target=\"_blank\">页岩气</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301365/\" target=\"_blank\">一带一路</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300188/\" target=\"_blank\">移动支付</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308638/\" target=\"_blank\">医疗废物处理</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308712/\" target=\"_blank\">医美概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301505/\" target=\"_blank\">医疗器械概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301279/\" target=\"_blank\">有机硅概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308752/\" target=\"_blank\">元宇宙</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301491/\" target=\"_blank\">粤港澳大湾区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301572/\" target=\"_blank\">玉米</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308634/\" target=\"_blank\">云办公</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300419/\" target=\"_blank\">云计算</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308624/\" target=\"_blank\">云游戏</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300917/\" target=\"_blank\">语音技术</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301565/\" target=\"_blank\">医药电商</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301227/\" target=\"_blank\">在线教育</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301223/\" target=\"_blank\">在线旅游</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301713/\" target=\"_blank\">中船系</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302046/\" target=\"_blank\">债转股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300642/\" target=\"_blank\">智慧城市</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301175/\" target=\"_blank\">智能穿戴</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300037/\" target=\"_blank\">智能电网</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300920/\" target=\"_blank\">智能家居</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300440/\" target=\"_blank\">智能交通</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301797/\" target=\"_blank\">智能物流</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300682/\" target=\"_blank\">智能医疗</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302131/\" target=\"_blank\">智能音箱</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308792/\" target=\"_blank\">智能制造</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308172/\" target=\"_blank\">知识产权保护</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301539/\" target=\"_blank\">职业教育</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301541/\" target=\"_blank\">中韩自贸区</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308690/\" target=\"_blank\">中芯国际概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300704/\" target=\"_blank\">中字头股票</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308740/\" target=\"_blank\">专精特新</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300435/\" target=\"_blank\">转基因</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/308697/\" target=\"_blank\">注册制次新股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300451/\" target=\"_blank\">猪肉</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301715/\" target=\"_blank\">证金持股</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301455/\" target=\"_blank\">摘帽</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/305794/\" target=\"_blank\">装配式建筑</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/301100/\" target=\"_blank\">足球概念</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/302034/\" target=\"_blank\">租售同权</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/306398/\" target=\"_blank\">自由贸易港</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                        <div class=\"cate_group\">\n" +
                "                <span class=\"cate_letter\">数字</span>\n" +
                "                <div class=\"cate_items\">\n" +
                "                                         <a href=\"http://q.10jqka.com.cn/gn/detail/code/300127/\" target=\"_blank\">3D打印</a>\n" +
                "                                        <a href=\"http://q.10jqka.com.cn/gn/detail/code/300843/\" target=\"_blank\">5G</a>\n" +
                "                                    </div>\n" +
                "            </div>\n" +
                "                       \n" +
                "        </div>";


        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(html)));
        var elements = DomUtils.getElementsByAttribute(document.getDocumentElement(), "class", "cate_items");

        for (var element : elements) {
            var items = DomUtils.getChildElements(element);
            for (var item : items) {
                var code = StringUtils.substringAfterFirst(item.getAttribute("href"), "http://q.10jqka.com.cn/gn/detail/code/");
                code = StringUtils.substringBeforeFirst(code, "/");

                var content = item.getTextContent();
                System.out.println(StringUtils.format("{}{}{}", code, StringUtils.TAB_ASCII, content));
            }
        }

    }


}
