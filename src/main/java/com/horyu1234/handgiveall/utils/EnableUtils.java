/*******************************************************************************
 * Copyright (c) 2014~2015 HoryuSystems All rights reserved.
 *
 * 본 저작물의 모든 저작권은 HoryuSystems 에 있습니다.
 *
 * 소스를 참고하여 다른 프로그램을 제작하는 것은 허용되지만,
 * 프로그램의 접두사, 기능등의 수정 및 배포는 불가능합니다.
 *
 * 소스에 대한 피드백등은 언제나 환영합니다! 아래는 개발자 연락처입니다.
 *
 * Skype: horyu1234
 * KakaoTalk: horyu1234
 * Telegram: @horyu1234
 ******************************************************************************/

package com.horyu1234.handgiveall.utils;

import com.horyu1234.handgiveall.HandGiveAll;
import com.horyu1234.handgiveall.web.DisableChecker;
import com.horyu1234.handgiveall.web.UpdateChecker;
import org.bukkit.Bukkit;

import javax.swing.*;
import java.io.File;

public class EnableUtils {
	private HandGiveAll plugin;
	public EnableUtils(HandGiveAll pl) {
		this.plugin = pl;
	}

	public boolean checkDisable() {
		if (DisableChecker.isDisable("HandGiveAll_v" + plugin.pluginversion)) {
			String reason = DisableChecker.getReason("HandGiveAll_v" + plugin.pluginversion);
			plugin.sendConsole("§c#==============================#");
			plugin.sendConsole("본 플러그인의 제작자가 플러그인의 구동을 비활성화하여");
			plugin.sendConsole("플러그인 구동이 제한됩니다.");
			plugin.sendConsole("");
			plugin.sendConsole("§4사유: ");
			plugin.sendConsole("  \"" + reason + "\"");
			plugin.sendConsole("§c#==============================#");
			Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HandGiveAll"), new Runnable() {
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) { }
					plugin.getServer().getPluginManager().disablePlugin(plugin);
				}
			});
			return true;
		}
		return false;
	}

	public void checkUpdate() {
		plugin.version = UpdateChecker.getVersion("HandGiveAll");
		if (plugin.version > plugin.pluginversion) {
			plugin.sendConsole("§b#==============================#");
			plugin.sendConsole("§f플러그인의 새로운 업데이트가 발견되었습니다!");
			plugin.sendConsole("§c현재버전: " + plugin.pluginversion);
			plugin.sendConsole("§a새로운버전: " + plugin.version);
			plugin.sendConsole("§e플러그인 다운로드 링크: https://horyu1234.com/HandGiveAll");
			plugin.sendConsole("§b#==============================#");
			Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HandGiveAll"), new Runnable() {
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) { }
				}
			});
		} else if (plugin.version < plugin.pluginversion) {
			plugin.sendConsole("§f#==============================#");
			plugin.sendConsole("§c서버에 올려진 플러그인의 버전보다 현재 플러그인의 버전이 높습니다.");
			plugin.sendConsole("§f#==============================#");
//			try {
//				Thread.sleep(10000L);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		} else if (plugin.version == plugin.pluginversion) {
			plugin.sendConsole("§f새로운 버전이 없습니다.");
		} else {
			plugin.sendConsole("§f#==============================#");
			plugin.sendConsole("§c플러그인의 버전을 확인하는데 문제가 발생했습니다.");
			plugin.sendConsole("§f#==============================#");
			Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HandGiveAll"), new Runnable() {
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) { }
				}
			});
		}
	}

	public void checkData() {
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			plugin.sendConsole("§c설정 파일을 찾을 수 없습니다. 새로 생성을 시작합니다...");
			plugin.saveDefaultConfig();
			plugin.sendConsole("§a완료");
			plugin.reloadConfig();
		}
		File a = new File("plugins/HandGiveAll/items");
		if (!a.exists()) {
			plugin.sendConsole("§c데이터 폴더를 찾을 수 없습니다. 새로 생성을 시작합니다...");
			a.mkdirs();
			plugin.sendConsole("§a완료");
		}
	}

	public boolean checkEULA() {
		if (!plugin.getConfig().getBoolean("EULA")) {
			plugin.sendConsole("§f#==============================#");
			plugin.sendConsole("§cConfig 에서 플러그인의 EULA에 동의해주세요!!");
			plugin.sendConsole("§cEULA를 동의하지 않으시면 플러그인의 사용이 불가능합니다.");
			plugin.sendConsole("§f#==============================#");
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Config 에서 플러그인의 EULA 에 동의해주시기 바랍니다.", "HandGiveAll v10.5", JOptionPane.ERROR_MESSAGE);
				}
			}).start();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return false;
		}
		return true;
	}

	public boolean checkConfigVersion() {
		if (plugin.getConfig().getDouble("config") != 1.4) {
			plugin.sendConsole("§f#==============================#");
			plugin.sendConsole("§cConfig 의 버전이 맞지 않습니다!");
			plugin.sendConsole("§cHandGiveAll 폴더 안의 config.yml 을 삭제하신 후 플러그인을 다시 실행해주시기 바랍니다.");
			plugin.sendConsole("§f#==============================#");
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Config 의 버전이 맞지 않습니다.", "HandGiveAll v10.5", JOptionPane.ERROR_MESSAGE);
				}
			}).start();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return true;
		}
		return false;
	}

	public void hookVault() {
		if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
			if (!plugin.setupEconomy()) {
				plugin.hookedVault = false;
				plugin.sendConsole("§f#==============================#");
				plugin.sendConsole("§cVault 와 연결하는데 실패했습니다.");
				plugin.sendConsole("§c돈 관련 명령어가 비활성화됩니다.");
				plugin.sendConsole("§f#==============================#");
			} else {
				plugin.hookedVault = true;
				plugin.sendConsole("§aVault 와 성공적으로 연결되었습니다.");
			}
		} else {
			plugin.hookedVault = false;
			plugin.sendConsole("§f#==============================#");
			plugin.sendConsole("§cVault 가 존재하지 않습니다.");
			plugin.sendConsole("§c돈 관련 명령어가 비활성화됩니다.");
			plugin.sendConsole("§f#==============================#");
		}
	}

	public boolean checkFileName() {
		try {
			File jar = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!jar.getName().equalsIgnoreCase("HandGiveAll v10.5.jar")) {
				plugin.sendConsole("§f#==============================#");
				plugin.sendConsole("§c플러그인 파일 이름 변경이 감지되었습니다.");
				plugin.sendConsole("§c정상 이름으로 변경해주시기 바랍니다.");
				plugin.sendConsole("");
				plugin.sendConsole("§f현재 이름: §c"+jar.getName());
				plugin.sendConsole("§f정상 이름: §aHandGiveAll v10.5.jar");
				plugin.sendConsole("§f#==============================#");
				new Thread(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(null, "플러그인 파일 이름 변경이 감지되었습니다.", "HandGiveAll v10.5", JOptionPane.ERROR_MESSAGE);
					}
				}).start();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return true;
			}
		} catch (Exception e) { }
		return false;
	}
}