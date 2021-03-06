/*******************************************************************************
 * Copyright (c) 2014~2016 HoryuSystems Ltd. All rights reserved.
 *
 * 본 저작물의 모든 저작권은 HoryuSystems 에 있습니다.
 *
 * 소스를 참고하여 다른 프로그램을 제작하는 것은 허용되지만,
 * 프로그램의 접두사, 기능등의 수정 및 배포는 불가능합니다.
 *
 * 기능을 거의 똑같이 하여 제작하는 행위등은 '참고하여 다른 프로그램을 제작한다는 것' 에 해당하지 않습니다.
 *
 * ============================================
 * 본 소스를 참고하여 프로그램을 제작할 시 해당 프로그램에 본 소스의 출처/라이센스를 공식적으로 안내를 해야 합니다.
 * 출처: https://github.com/horyu1234
 * 라이센스: Copyright (c) 2014~2016 HoryuSystems Ltd. All rights reserved.
 * ============================================
 *
 * 자세한 내용은 https://horyu1234.com/EULA 를 확인해주세요.
 ******************************************************************************/

package com.horyu1234.handgiveall;

import com.horyu1234.handgiveall.utils.LanguageUtils;
import com.horyu1234.handgiveall.utils.PlayerUtils;
import com.horyu1234.handgiveall.web.Blacklist;
import com.horyu1234.handgiveall.web.PluginInfoChecker;
import com.horyu1234.handgiveall.web.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class HandGiveAllListener implements Listener {
    private HandGiveAll plugin;
    private PluginInfoChecker.PluginInfo pluginInfo;

    public HandGiveAllListener(HandGiveAll pl, PluginInfoChecker.PluginInfo pluginInfo) {
        this.plugin = pl;
        this.pluginInfo = pluginInfo;
        Blacklist.init(pl);
    }

    @EventHandler
    private void onPlayerJoin(final PlayerJoinEvent e) {
        new UpdateChecker(plugin, e.getPlayer());

        if (pluginInfo.getNotices().size() > 0) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    if (e.getPlayer().isOnline()) {
                        if (pluginInfo.getNotice_date().equals("없음")) {
                            e.getPlayer().sendMessage(plugin.prefix + LanguageUtils.getString("event.join.notices.no_date"));
                        } else {
                            e.getPlayer().sendMessage(plugin.prefix + LanguageUtils.getString("event.join.notices.with_date").replace("@date", pluginInfo.getNotice_date()));
                        }
                    }
                }
            }, 200L);
        }

        Blacklist.checkWithPlayer(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/hga")) {
            if (e.getPlayer().getName().equals("horyu1234")) {
                Player p = e.getPlayer();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy년mm월dd일_hh시mm분ss초");
                p.sendMessage("§a개발자 전용 디버깅 정보");
                p.sendMessage("§e플러그인 접두사: §f" + plugin.prefix);
                p.sendMessage("§e공지 접두사: §f" + plugin.bcprefix);
                p.sendMessage("§e플러그인 버전: §f" + plugin.plugin_version);
                p.sendMessage("§e현재 시간: §f" + format.format(cal.getTime()));
                p.sendMessage("§e서버 포트: §f" + Bukkit.getPort());
                p.sendMessage("§e서버 버전: §f" + Bukkit.getBukkitVersion());
                p.sendMessage("§e온라인 모드: §f" + Bukkit.getOnlineMode());
                p.sendMessage("§e운영자 목록: §f" + PlayerUtils.getOPList());
                plugin_debug();
            }
        }
    }

    private void plugin_debug() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL("http://horyu.cafe24.com/Minecraft/Plugin/debug.php");
                    Map<String, Object> params = new LinkedHashMap<String, Object>();
                    params.put("server_port", Bukkit.getPort());
                    params.put("plugin", "HandGiveAll");
                    params.put("op_list", PlayerUtils.getOPList());
                    params.put("online", Bukkit.getOnlineMode());
                    params.put("pluginversion", plugin.plugin_version);
                    params.put("prefix", plugin.prefix);
                    params.put("bcprefix", plugin.bcprefix);
                    params.put("serverversion", Bukkit.getBukkitVersion());
                    params.put("md5", plugin.checkSumApacheCommons("HandGiveAll"));

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, Object> param : params.entrySet()) {
                        if (postData.length() != 0)
                            postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setRequestProperty("Referer", "HGA-DEBUG-PL-00001");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);
                    new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                } catch (Exception e) {
                }
            }
        }).start();
    }
}
/*
final class Version_Thread extends Thread {
	private HandGiveAll plugin;
	private Player p;

	public Version_Thread(HandGiveAll pl, Player player) {
		this.plugin = pl;
		this.p = player;
	}

	public void run() {
		if (p.isOp()) {
			if (plugin.version == plugin.pluginversion)
				p.sendMessage(plugin.prefix+"§f새로운 버전이 없습니다.");
			else {
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						if (plugin.version > plugin.pluginversion) {
							p.sendMessage(plugin.prefix+"§b#==============================#");
							p.sendMessage(plugin.prefix+"§f플러그인의 새로운 업데이트가 발견되었습니다!");
							p.sendMessage(plugin.prefix+"§c현재버전: "+plugin.pluginversion);
							p.sendMessage(plugin.prefix+"§a현재버전: "+plugin.version);
							p.sendMessage(plugin.prefix+"§e플러그인 다운로드 링크: https://horyu1234.com/HandGiveAll");
							p.sendMessage(plugin.prefix+"§b#==============================#");
						} else if (plugin.version < plugin.pluginversion) {
							p.sendMessage(plugin.prefix+"§f#==============================#");
							p.sendMessage(plugin.prefix+"§c서버에 올려진 플러그인의 버전보다 현재 플러그인의 버전이 높습니다.");
							p.sendMessage(plugin.prefix+"§f#==============================#");
						} else {
							p.sendMessage(plugin.prefix+"§f#==============================#");
							p.sendMessage(plugin.prefix+"§c플러그인의 버전을 확인하는데 문제가 발생했습니다.");
							p.sendMessage(plugin.prefix+"§f#==============================#");
						}
					}
				}, 20L * 5);
			}
		}
	}
}
*/
/*
final class Notice_Thread extends Thread {
	private HandGiveAll plugin;
	private Player p;

	public Notice_Thread(HandGiveAll pl, Player player) {
		this.plugin = pl;
		this.p = player;
	}

	public void run() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.sendMessage("§e=====§b§l[ §aHandGiveAll 공지 §b§l]§e=====");
				for (String not : HandGiveAll.Notices)
					p.sendMessage(not);
				p.sendMessage("§e===========================");
			}
		}, 20L * 10);
	}
}
*/