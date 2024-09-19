package red.bread.graidar.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraidarClient implements ClientModInitializer {
    public static String currentGuild = null;

    private final static String endpoint = "<SET YOUR ENDPOINT HERE>";
    private final static String guildPrefix1 = "\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE";
    private final static String guildPrefix2 = "\uDAFF\uDFFC\uE001\uDB00\uDC06";

    @Override
    public void onInitializeClient() {

    }

    private static String getUnformattedString(String string) {
        return string
                .replaceAll(guildPrefix1, "")
                .replaceAll(guildPrefix2, "")
                .replaceAll("§.", "")
                .replaceAll("&.", "")
                .replaceAll("\\[[0-9:]+]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static void onMessage(Text message) {
        tryLogRaidMessage(message);
        tryLogAspectReward(message);
    }

    private static void tryLogRaidMessage(Text message) {
        final String regex = "([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), ([A-Za-z0-9_ ]+?), and ([A-Za-z0-9_ ]+?) finished (.+?) and claimed (\\d+)x Aspects, (\\d+)x Emeralds, and \\+(\\d+) Seasonal Rating";
        String unformattedMessage = getUnformattedString(message.getString());
        Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(unformattedMessage);
        HashMap<String, List<String>> nameMap = new HashMap<>();
        GetRealName.createRealNameMap(message, nameMap);
        if (!matcher.matches()) return;
        String user1 = matcher.group(1);
        if (nameMap.containsKey(user1)) {
            user1 = nameMap.get(user1).removeLast();
        }
        String user2 = matcher.group(2);
        if (nameMap.containsKey(user2)) {
            user2 = nameMap.get(user2).removeLast();
        }
        String user3 = matcher.group(3);
        if (nameMap.containsKey(user3)) {
            user3 = nameMap.get(user3).removeLast();
        }
        String user4 = matcher.group(4);
        if (nameMap.containsKey(user4)) {
            user4 = nameMap.get(user4).removeLast();
        }
        String raid = matcher.group(5);
        String aspects = matcher.group(6);
        String emeralds = matcher.group(7);
        String sr = matcher.group(8);
        if (MinecraftClient.getInstance().player != null) {
            final String data = String.format(
                            "{`type`:`graid`,`timestamp`:%s,`message`:`%s`,`guild`:`%s`,`username`:`%s`,`user1`:`%s`,`user2`:`%s`,`user3`:`%s`,`user4`:`%s`,`raid`:`%s`,`aspects`:`%s`,`emeralds`:`%s`,`sr`:`%s`}",
                            System.currentTimeMillis(),
                            unformattedMessage,
                            currentGuild,
                            MinecraftClient.getInstance().player.getName().getString(),
                            user1,
                            user2,
                            user3,
                            user4,
                            raid,
                            aspects,
                            emeralds,
                            sr
                    )
                    .replace('`', '"');
            new Thread(() -> WebRequest.postData(endpoint, data)).start();
        }
    }

    private static void tryLogAspectReward(Text message) {
        final String regex = "([A-Za-z0-9_ ]+?) rewarded an Aspect to ([A-Za-z0-9_ ]+?)";
        String unformattedMessage = getUnformattedString(message.getString());
        Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(unformattedMessage);
        HashMap<String, List<String>> nameMap = new HashMap<>();
        GetRealName.createRealNameMap(message, nameMap);
        if (!matcher.matches()) return;
        String giver = matcher.group(1);
        if (nameMap.containsKey(giver)) {
            giver = nameMap.get(giver).removeFirst();
        }
        String receiver = matcher.group(2);
        if (nameMap.containsKey(receiver)) {
            receiver = nameMap.get(receiver).removeFirst();
        }

        if (MinecraftClient.getInstance().player != null) {
            final String data = String.format(
                            "{`type`:`aspect`,`timestamp`:%s,`message`:`%s`,`guild`:`%s`,`username`:`%s`,`giver`:`%s`,`receiver`:`%s`}",
                            System.currentTimeMillis(),
                            unformattedMessage,
                            currentGuild,
                            MinecraftClient.getInstance().player.getName().getString(),
                            giver,
                            receiver
                    )
                    .replace('`', '"');
            new Thread(() -> WebRequest.postData(endpoint, data)).start();
        }
    }

    public static void onRenderBossBar(BossBar bossBar) {
        try {
            String bossBarName = getUnformattedString(bossBar.getName().getString());
            Pattern pattern = Pattern.compile("(?<=Lv. \\d{1,3} - ).*(?= - \\d{1,3}% XP)");
            Matcher matcher = pattern.matcher(bossBarName);
            if (!matcher.find()) return;
            currentGuild = matcher.group(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
