package com.richguy.stock;

import com.richguy.model.stock.Stock;
import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Ignore
public class StockParseTest {

    @Test
    public void test() throws ParserConfigurationException, IOException, SAXException {
        var str = "<table class=\"m-table m-pager-table\">\n" +
                "    <thead>\n" +
                "    <tr>\n" +
                "        <th style=\"width:4%\">序号</th>\n" +
                "        <th style=\"width:6%\">代码</th>\n" +
                "        <th style=\"width:8%\">名称</th>\n" +
                "        <th style=\"width:6%\"><a href=\"javascript:void(0)\" field=\"xj\">现价<i></i></a></th>\n" +
                "        <th style=\"width:8%\" class=\"cur\"><a href=\"javascript:void(0)\" field=\"zdf\" order=\"desc\"\n" +
                "                                            class=\"desc\">涨跌幅(%)<i></i></a></th>\n" +
                "        <th style=\"width:6%\"><a href=\"javascript:void(0)\" field=\"zd\">涨跌<i></i></a></th>\n" +
                "        <th style=\"width:8%\"><a href=\"javascript:void(0)\" field=\"zs\">涨速(%)<i></i></a></th>\n" +
                "        <th style=\"width:8%\"><a href=\"javascript:void(0)\" field=\"hs\">换手(%)<i></i></a></th>\n" +
                "        <th style=\"width:6%\"><a href=\"javascript:void(0)\" field=\"lb\">量比<i></i></a></th>\n" +
                "        <th style=\"width:6%\"><a href=\"javascript:void(0)\" field=\"zf\">振幅(%)<i></i></a></th>\n" +
                "        <th style=\"width:7%\"><a href=\"javascript:void(0)\" field=\"cje\">成交额<i></i></a></th>\n" +
                "        <th style=\"width:8%\"><a href=\"javascript:void(0)\" field=\"ltg\">流通股<i></i></a></th>\n" +
                "        <th style=\"width:8%\"><a href=\"javascript:void(0)\" field=\"ltsz\">流通市值<i></i></a></th>\n" +
                "        <th style=\"width:7%\"><a href=\"javascript:void(0)\" field=\"syl\">市盈率<i></i></a></th>\n" +
                "        <!--th>概念题材</th-->\n" +
                "        <th style=\"width:4%\">加自选</th>\n" +
                "    </tr>\n" +
                "    </thead>\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td>41</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002837/\" target=\"_blank\">002837</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002837/\" target=\"_blank\">英维克</a></td>\n" +
                "        <td class=\"c-rise\">46.82</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">4.26</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>4.55</td>\n" +
                "        <td class=\"c-rise\">2.35</td>\n" +
                "        <td class=\"c-rise\">7.80</td>\n" +
                "        <td>5.37亿</td>\n" +
                "        <td>2.56亿</td>\n" +
                "        <td>119.81亿</td>\n" +
                "        <td>77.53</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>42</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002169/\" target=\"_blank\">002169</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002169/\" target=\"_blank\">智光电气</a></td>\n" +
                "        <td class=\"c-rise\">12.42</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">1.13</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>6.21</td>\n" +
                "        <td class=\"c-rise\">4.06</td>\n" +
                "        <td class=\"c-rise\">4.78</td>\n" +
                "        <td>5.86亿</td>\n" +
                "        <td>7.63亿</td>\n" +
                "        <td>94.82亿</td>\n" +
                "        <td>11.33</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>43</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600559/\" target=\"_blank\">600559</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600559/\" target=\"_blank\">老白干酒</a></td>\n" +
                "        <td class=\"c-rise\">28.03</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">2.55</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>9.67</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">11.26</td>\n" +
                "        <td>23.53亿</td>\n" +
                "        <td>8.97亿</td>\n" +
                "        <td>251.51亿</td>\n" +
                "        <td>75.62</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>44</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002190/\" target=\"_blank\">002190</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002190/\" target=\"_blank\">成飞集成</a></td>\n" +
                "        <td class=\"c-rise\">32.76</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">2.98</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>3.20</td>\n" +
                "        <td class=\"c-rise\">2.55</td>\n" +
                "        <td class=\"c-rise\">10.61</td>\n" +
                "        <td>3.68亿</td>\n" +
                "        <td>3.59亿</td>\n" +
                "        <td>117.52亿</td>\n" +
                "        <td>215.33</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>45</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603305/\" target=\"_blank\">603305</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603305/\" target=\"_blank\">旭升股份</a></td>\n" +
                "        <td class=\"c-rise\">48.37</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">4.40</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.14</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">8.66</td>\n" +
                "        <td>2.41亿</td>\n" +
                "        <td>4.47亿</td>\n" +
                "        <td>216.23亿</td>\n" +
                "        <td>48.88</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>46</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600588/\" target=\"_blank\">600588</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600588/\" target=\"_blank\">用友网络</a></td>\n" +
                "        <td class=\"c-rise\">35.07</td>\n" +
                "        <td class=\"c-rise\">10.01</td>\n" +
                "        <td class=\"c-rise\">3.19</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.41</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">9.66</td>\n" +
                "        <td>15.61亿</td>\n" +
                "        <td>32.41亿</td>\n" +
                "        <td>1136.66亿</td>\n" +
                "        <td>677.66</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>47</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002813/\" target=\"_blank\">002813</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002813/\" target=\"_blank\">路畅科技</a></td>\n" +
                "        <td class=\"c-rise\">27.82</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">2.53</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>4.27</td>\n" +
                "        <td class=\"c-rise\">1.09</td>\n" +
                "        <td class=\"c-rise\">10.68</td>\n" +
                "        <td>1.36亿</td>\n" +
                "        <td>1.17亿</td>\n" +
                "        <td>32.65亿</td>\n" +
                "        <td>453.14</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>48</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600696/\" target=\"_blank\">600696</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600696/\" target=\"_blank\">岩石股份</a></td>\n" +
                "        <td class=\"c-rise\">33.77</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">3.07</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.49</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td>1.63亿</td>\n" +
                "        <td>3.34亿</td>\n" +
                "        <td>112.95亿</td>\n" +
                "        <td>102.62</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>49</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600192/\" target=\"_blank\">600192</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600192/\" target=\"_blank\">长城电工</a></td>\n" +
                "        <td class=\"c-rise\">5.17</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.47</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>6.57</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">9.15</td>\n" +
                "        <td>1.47亿</td>\n" +
                "        <td>4.42亿</td>\n" +
                "        <td>22.84亿</td>\n" +
                "        <td>亏损</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>50</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000537/\" target=\"_blank\">000537</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000537/\" target=\"_blank\">广宇发展</a></td>\n" +
                "        <td class=\"c-rise\">23.10</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">2.10</td>\n" +
                "        <td class=\"c-rise\">0.57</td>\n" +
                "        <td>3.27</td>\n" +
                "        <td class=\"c-rise\">2.67</td>\n" +
                "        <td class=\"c-rise\">9.43</td>\n" +
                "        <td>12.79亿</td>\n" +
                "        <td>17.31亿</td>\n" +
                "        <td>399.95亿</td>\n" +
                "        <td>亏损</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>51</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603985/\" target=\"_blank\">603985</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603985/\" target=\"_blank\">恒润股份</a></td>\n" +
                "        <td class=\"c-rise\">51.04</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">4.64</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>2.94</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">7.26</td>\n" +
                "        <td>3.89亿</td>\n" +
                "        <td>2.65亿</td>\n" +
                "        <td>135.25亿</td>\n" +
                "        <td>38.50</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>52</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603339/\" target=\"_blank\">603339</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/603339/\" target=\"_blank\">四方科技</a></td>\n" +
                "        <td class=\"c-rise\">12.65</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">1.15</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>4.11</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">9.83</td>\n" +
                "        <td>1.59亿</td>\n" +
                "        <td>3.09亿</td>\n" +
                "        <td>39.14亿</td>\n" +
                "        <td>22.44</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>53</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000595/\" target=\"_blank\">000595</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000595/\" target=\"_blank\">宝塔实业</a></td>\n" +
                "        <td class=\"c-rise\">2.86</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.26</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>2.40</td>\n" +
                "        <td class=\"c-rise\">7.85</td>\n" +
                "        <td class=\"c-rise\">11.15</td>\n" +
                "        <td>7709.79万</td>\n" +
                "        <td>11.36亿</td>\n" +
                "        <td>32.49亿</td>\n" +
                "        <td>亏损</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>54</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002513/\" target=\"_blank\">002513</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002513/\" target=\"_blank\">蓝丰生化</a></td>\n" +
                "        <td class=\"c-rise\">5.61</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.51</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.71</td>\n" +
                "        <td class=\"c-rise\">3.03</td>\n" +
                "        <td class=\"c-rise\">5.69</td>\n" +
                "        <td>3001.46万</td>\n" +
                "        <td>3.14亿</td>\n" +
                "        <td>17.64亿</td>\n" +
                "        <td>亏损</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>55</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002534/\" target=\"_blank\">002534</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002534/\" target=\"_blank\">杭锅股份</a></td>\n" +
                "        <td class=\"c-rise\">26.84</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">2.44</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>0.87</td>\n" +
                "        <td class=\"c-rise\">1.95</td>\n" +
                "        <td class=\"c-rise\">11.43</td>\n" +
                "        <td>1.65亿</td>\n" +
                "        <td>7.26亿</td>\n" +
                "        <td>194.76亿</td>\n" +
                "        <td>41.64</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>56</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000678/\" target=\"_blank\">000678</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/000678/\" target=\"_blank\">襄阳轴承</a></td>\n" +
                "        <td class=\"c-rise\">4.95</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.45</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.66</td>\n" +
                "        <td class=\"c-rise\">4.76</td>\n" +
                "        <td class=\"c-rise\">10.22</td>\n" +
                "        <td>3648.78万</td>\n" +
                "        <td>4.60亿</td>\n" +
                "        <td>22.75亿</td>\n" +
                "        <td>亏损</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>57</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600335/\" target=\"_blank\">600335</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600335/\" target=\"_blank\">国机汽车</a></td>\n" +
                "        <td class=\"c-rise\">10.12</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.92</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>4.78</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">11.63</td>\n" +
                "        <td>4.86亿</td>\n" +
                "        <td>10.30亿</td>\n" +
                "        <td>104.21亿</td>\n" +
                "        <td>58.55</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>58</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600550/\" target=\"_blank\">600550</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/600550/\" target=\"_blank\">保变电气</a></td>\n" +
                "        <td class=\"c-rise\">5.39</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">0.49</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>1.22</td>\n" +
                "        <td class=\"\">--</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td>1.19亿</td>\n" +
                "        <td>18.42亿</td>\n" +
                "        <td>99.26亿</td>\n" +
                "        <td>531.54</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>59</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/003043/\" target=\"_blank\">003043</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/003043/\" target=\"_blank\">华亚智能</a></td>\n" +
                "        <td class=\"c-rise\">92.41</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">8.40</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>11.36</td>\n" +
                "        <td class=\"c-rise\">2.89</td>\n" +
                "        <td class=\"c-rise\">11.02</td>\n" +
                "        <td>2.06亿</td>\n" +
                "        <td>2000.00万</td>\n" +
                "        <td>18.48亿</td>\n" +
                "        <td>66.99</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td>60</td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002943/\" target=\"_blank\">002943</a></td>\n" +
                "        <td><a href=\"http://stockpage.10jqka.com.cn/002943/\" target=\"_blank\">宇晶股份</a></td>\n" +
                "        <td class=\"c-rise\">45.99</td>\n" +
                "        <td class=\"c-rise\">10.00</td>\n" +
                "        <td class=\"c-rise\">4.18</td>\n" +
                "        <td class=\"\">0.00</td>\n" +
                "        <td>10.60</td>\n" +
                "        <td class=\"c-rise\">1.07</td>\n" +
                "        <td class=\"c-rise\">3.90</td>\n" +
                "        <td>3.16亿</td>\n" +
                "        <td>6540.13万</td>\n" +
                "        <td>30.08亿</td>\n" +
                "        <td>1817.27</td>\n" +
                "        <td><a class=\"j_addStock\" title=\"加自选\" href=\"javascript:void(0);\"><img\n" +
                "                src=\"http://i.thsi.cn/images/q/plus_logo.png\" alt=\"\"></a></td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "<input type=\"hidden\" id=\"request\" value='{\"board\":\"all\",\"field\":\"zdf\",\"order\":\"desc\",\"page\":\"3\",\"ajax\":\"1\"}'>\n" +
                "<input type=\"hidden\" id=\"baseUrl\" value='index/index'>\n" +
                "<div class=\"m-pager\" id=\"m-page\">\n" +
                "    &nbsp;<a class=\"changePage\" page=\"1\" href=\"javascript:void(0);\">首页</a>&nbsp;<a class=\"changePage\" page=\"2\"\n" +
                "                                                                                   href=\"javascript:void(0);\">上一页</a>&nbsp;&nbsp;<a\n" +
                "        class=\"changePage\" page=\"1\" href=\"javascript:void(0);\">1</a>&nbsp;&nbsp;<a class=\"changePage\" page=\"2\"\n" +
                "                                                                                   href=\"javascript:void(0);\">2</a>&nbsp;&nbsp;<a\n" +
                "        class=\"cur\" page=\"3\" href=\"javascript:void(0)\">3</a>&nbsp;&nbsp;<a class=\"changePage\" page=\"4\"\n" +
                "                                                                           href=\"javascript:void(0);\">4</a>&nbsp;&nbsp;<a\n" +
                "        class=\"changePage\" page=\"5\" href=\"javascript:void(0);\">5</a>&nbsp;&nbsp;<a class=\"changePage\" page=\"4\"\n" +
                "                                                                                   href=\"javascript:void(0);\">下一页</a><a\n" +
                "        class=\"changePage\" page=\"227\" href=\"javascript:void(0);\">尾页</a><span class=\"page_info\">3/227</span>\n" +
                "</div>\n";

        str = StringUtils.substringAfterFirst(str, "<tbody>");
        str = StringUtils.substringBeforeLast(str, "</tbody>");
        str = StringUtils.format("<tbody> {} </tbody>", str);
        str = str.replaceAll("alt=\"\">", "/>");

        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(new ByteArrayInputStream(StringUtils.bytes(str)));
        var stockElements = DomUtils.getChildElements(document.getDocumentElement());
        var stocks = new ArrayList<Stock>();
        for (var stockElement : stockElements) {
            var stockAttributes = DomUtils.getChildElements(stockElement);
            var code = stockAttributes.get(1).getTextContent();
            var name = stockAttributes.get(2).getTextContent();
            var nowPrice = stockAttributes.get(3).getTextContent();
            var riseRatio = stockAttributes.get(4).getTextContent();
            var riseNum = stockAttributes.get(5).getTextContent();
            var increaseRatio = stockAttributes.get(6).getTextContent();
            var turnoverRatio = stockAttributes.get(7).getTextContent();
            var volumeRatio = stockAttributes.get(8).getTextContent();
            var vibration = stockAttributes.get(9).getTextContent();
            var turnover = stockAttributes.get(10).getTextContent();
            var floatingStock = stockAttributes.get(11).getTextContent();
            var marketValue = stockAttributes.get(12).getTextContent();
            var pe = stockAttributes.get(13).getTextContent();

            System.out.println(stockAttributes.get(0).getTextContent());
        }

        System.out.println(str);
    }

}
