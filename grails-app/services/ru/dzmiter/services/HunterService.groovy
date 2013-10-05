package ru.dzmiter.services

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ru.dzmiter.domain.HuntReportItem
import ru.dzmiter.utils.Transliterator

/**
 * @author Dzmitry Bezugly
 */
class HunterService {

    static reports = []

    void travel(String vkUserId, String authKey, int locationId) {
        final String URL_PATTERN = "http://mice2112.com/travel/changeLocation/";
        final String URL_PARAMETERS = "auth_key=$authKey&free=1&location_id=$locationId&viewer_id=$vkUserId";
        doPOSTRequest(URL_PATTERN, URL_PARAMETERS, vkUserId);
    }

    //blue cheese - cheeseId = 13, locationId = 2
    void buyCheese(String vkUserId, String authKey, int cheeseId, int count, int locationId) {
        final String URL_PATTERN = "http://mice2112.com/shop/buy/";
        final String URL_PARAMETERS = "auth_key=$authKey&count=$count&eid=$cheeseId" +
                "&free=1&location_id=$locationId&viewer_id=$vkUserId";
        doPOSTRequest(URL_PATTERN, URL_PARAMETERS, vkUserId);
    }

    //blue id = 4
    void setActiveCheese(String vkUserId, String authKey, int id) {
        final String URL_PATTERN = "http://mice2112.com/game/set-active-cheese/";
        final String URL_PARAMETERS = "auth_key=$authKey&is_boss_camp=0&item_id=$id&type=arm&viewer_id=$vkUserId";
        doPOSTRequest(URL_PATTERN, URL_PARAMETERS);
    }

    void controlCheeseCount(String response, String vkUserId, String authKey) {
        if (!response || !vkUserId || !authKey) return;

        String start = "Application.updateParamCampActiveCheeseCount(";
        String end = ");";
        int startIndex = response.indexOf(start);
        int endIndex = response.indexOf(end, startIndex);

        if (startIndex == -1 || endIndex == -1) return;

        String[] activeCheeseInfo = response[startIndex + start.size()..endIndex - 1]?.split(",");
        if (!activeCheeseInfo || activeCheeseInfo.size() <= 0) return;
        Integer count = activeCheeseInfo[0] ==~ /\d+/ ? Integer.decode(activeCheeseInfo[0]) : 0;

        if (count < 10) {
            start = "Application.updateParamHeadGold(";
            end = ");";
            startIndex = response.indexOf(start);
            endIndex = response.indexOf(end, startIndex);

            if (startIndex == -1 || endIndex == -1) return;

            String money = response[startIndex + start.size()..endIndex - 1];
            if (!money) return;
            Integer gold = money ==~ /\d+/ ? Integer.decode(money) : 0;

            int cost = 200; //200 gold - 1 cheese

            if (cost * 200 < gold) {
                buyCheese(vkUserId, authKey, 13, 200, 2);
                setActiveCheese(vkUserId, authKey, 4);
            } else {
                int cnt = (int) gold / cost;
                buyCheese(vkUserId, authKey, 13, cnt, 2);
                setActiveCheese(vkUserId, authKey, 4);
            }
        }
    }

    void hunt(String vkUserId, String authKey) {
        final String URL_PATTERN = "http://mice2112.com/hunt/start/";
        final String URL_PARAMETERS = "auth_key=$authKey&viewer_id=$vkUserId";
        String response = doPOSTRequest(URL_PATTERN, URL_PARAMETERS);
        String answer = parseHuntResponse(response);
        if(reports.size() > 50) {
            reports = Arrays.copyOfRange(reports, reports.size() - 50, reports.size());
        }
        if (answer) {
            controlCheeseCount(response, vkUserId, authKey);
            reports << new HuntReportItem(date: new Date(), report: Transliterator.translit(answer), user: vkUserId)
            return
        }
        reports << new HuntReportItem(date: new Date(), report: Transliterator.translit(response), user: vkUserId)
    }

    void getBonus(String vkUserId, String authKey) {
        final String URL_PATTERN = "http://mice2112.com/game/bonus";
        final String URL_PARAMETERS = "auth_key=$authKey&viewer_id=$vkUserId";
        doPOSTRequest(URL_PATTERN, URL_PARAMETERS);
    }

    Integer getCheeseCount(String vkUserId, String authKey) {
        final String URL_PATTERN = "http://mice2112.com/profile/index";
        final String URL_PARAMETERS = "auth_key=$authKey&dojo.preventCache=&friends=&param=&viewer_id=$vkUserId";
        String response = doPOSTRequest(URL_PATTERN, URL_PARAMETERS);
        return parseCheeseCount(response);
    }

    String parseHuntResponse(String response) {
        if (!response) return null;
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("div.journal-b-p");
        if (elements) {
            return elements.get(0)?.text();
        }
    }

    Integer parseCheeseCount(String response) {
        if (!response) return null;
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select("dl.style-dl1");
        if (elements) {
            Elements dts = elements.get(0)?.select("dd");
            int size = dts?.size();
            String cheeseCount;
            if (size > 5) cheeseCount = dts.get(5)?.text()?.find(/\d+/);
            if (cheeseCount && cheeseCount ==~ /\d+/) return Integer.decode(cheeseCount);
        }
    }

    String parseJsonResponse(String json) {
        if (!json) return null;
        JSONParser jsonParser = new JSONParser();
        JSONObject object = jsonParser.parse(json);
        object?.get("message");
    }

    String doPOSTRequest(String url, String parameters) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        return response.toString();
    }

}