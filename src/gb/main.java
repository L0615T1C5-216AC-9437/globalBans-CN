package gb;

import arc.*;
import arc.util.*;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.net.Packets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class main extends Plugin{
    public static HashMap<String, Long> cache = new HashMap<>();
    //called when game initializes
    @Override
    public void init(){
        Events.on(EventType.PlayerJoin.class, event -> {
            Player player = event.player;
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("http://chaotic-neutral.ddns.net:8080/badListData?uuidip="+player.uuid()).openConnection();
                con.setConnectTimeout(10000);
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (con.getResponseCode() == 200) {
                    String[] data = response.toString().substring(1, response.toString().length() - 2).split(",");
                    if (!data[1].contains("null")) {
                        long kick = Strings.parseLong(data[1].substring(data[1].indexOf(":")+1), 0);
                        if (kick > System.currentTimeMillis()) {
                            player.con.kick(Packets.KickReason.recentKick);
                            return;
                        }
                    }
                    if (!data[2].contains("null") && !data[2].contains("false"))
                        player.con.kick(Packets.KickReason.recentKick);
                } else {
                    Log.debug("State:"+con.getResponseCode()+" - "+response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
