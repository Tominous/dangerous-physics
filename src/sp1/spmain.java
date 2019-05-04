package sp1;

import java.awt.Event;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftSound;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Arrow.PickupStatus;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBed;
import net.minecraft.server.v1_12_R1.PacketPlayOutGameStateChange;

public class spmain extends JavaPlugin implements Listener, CommandExecutor{

	BukkitScheduler scheduler = null;
	public ConsoleCommandSender console = getServer().getConsoleSender();
	FileConfiguration config = getConfig();
	Random randor = new Random();
	public World wor = null;
	public int radius = 0;
	public durabilities dur = new durabilities();
	DecimalFormat df = new DecimalFormat("#.####");
	public HashMap<Block, Integer> tempers = new HashMap<Block, Integer>();
	public HashMap<Player, Integer> powers = new HashMap<Player, Integer>();
	public List<Entity> effectEnts = new ArrayList<Entity>();
	public HashMap<Entity, Long> armors = new HashMap<Entity, Long>();
	public boolean doRealisticDrops = false;
	public boolean dyTemp = true;
	public List<Block> fires = new ArrayList<Block>();
	public List<Block> firesT = new ArrayList<Block>();
	public List<Entity> fallingsands = new ArrayList<Entity>();
	public blockSounds bs = new blockSounds();
	int typess = 0;
	short datass = 0;
	public List<Player> wand = new ArrayList<Player>();
	public boolean doRealisticSpreading = false;
	public List<gasManager> gsM = new ArrayList<gasManager>();
	public boolean doWaterPhysics = false;
	
	@Override
	public void onEnable() {
		createConfigFol();
		gasManager smoke = new gasManager("smoke", Particle.SMOKE_LARGE, 1, 0.6);
		gasManager steam = new gasManager("steam", Particle.CLOUD, 1, .7);
		gasManager water = new gasManager("water", Particle.BLOCK_CRACK, 0, 1);
		gsM.add(smoke);
		gsM.add(steam);
		gsM.add(water);
		this.getServer().getPluginManager().registerEvents(this, this);
		if(!this.getServer().getOnlinePlayers().isEmpty()) {
			for(Player start : this.getServer().getOnlinePlayers()) {
				wor = start.getWorld();
				break;
			}
		}
		scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				removeTemp();
			}
		}, 0L, /* 600 */((long) 3300));
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for(gasManager gm : gsM) {
				gm.doParticleEffects();
				}
				doGasEffects();
			}
		}, 0L, /* 600 */10L);
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if(doRealisticDrops) {
				deleteArmor();
				}
			}
		}, 0L, /* 600 */35L);
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				betterEffectLooper();
				doDynamicTemperature();
				for(gasManager gm : gsM) {
					gm.doGravity();
				}
			}
		}, 0L, /* 600 */((long) 3));
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				runFireTemp();
				for(gasManager gm : gsM) {
				gm.checkLives();
				}
			}
		}, 0L, /* 600 */((long) 700));
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if(doRealisticDrops) {
				checkArmor();
				}
				dofallingsands();
			}
		}, 0L, /* 600 */((long) 1));
	}
	
	@Override
	public void onDisable() {
		wor=null;
	}
	

	public void createConfigFol() {
		config.addDefault("Enable Block Physics ", false);
		config.addDefault("Enable Temperature ", true);
		config.addDefault("Enable Fire Smoke ", true);
		config.addDefault("Enable Steam ", true);
		config.addDefault("Enable Enhanced Fires ", true);
		config.addDefault("Enable Enhanced Explosions ", true);
		config.addDefault("Enable Ash ", true);
		config.addDefault("Enable 3D Drops ", false);
		config.addDefault("Enable Netherrack ", false);
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	
	public void checkArmor() {
		if(!armors.isEmpty()) {
			HashMap<Entity, Long> armors2 = new HashMap<Entity, Long>(armors);
			for(Entity ee : armors2.keySet()) {
				if(ee!=null) {
					if(ee.getFireTicks()>0) {
					if(ee.isOnGround()&&ee.getLocation().getBlock().getType()==Material.AIR) {
						ee.teleport(ee.getLocation().subtract(0, .3, 0));
						ee.setGravity(false);
					}
					else if(ee.getLocation().getBlock().getType()!=Material.AIR) {
						
					}
					else {
						ee.setGravity(true);
						ee.setVelocity(new Vector(0,-.3,0));
					}
				}
				}
			}
		}
	}
	
	public void placeFallingSand(Entity ee, int check) {
		Location dothing = ee.getLocation();
		boolean cando = false;
		if(ee.getLocation().getBlock().getType()==Material.LONG_GRASS||ee.getLocation().getBlock().getType()==Material.AIR||(ee.getLocation().getBlock().getType()==Material.STATIONARY_LAVA||ee.getLocation().getBlock().getType()==Material.LAVA||ee.getLocation().getBlock().getType()==Material.STATIONARY_WATER||ee.getLocation().getBlock().getType()==Material.WATER)) {
			cando=true;
		}
		if(cando == false) {
			Location ltemp = ee.getLocation().clone().add(0, 1, 0);
			for(int i = 0; i < 16; i++) {
				Material newm = ltemp.clone().add(0, i, 0).getBlock().getType();
				if(newm==Material.AIR) {
					dothing = ltemp.clone().add(0, i, 0);
					break;
				}
			}
		}
		if(check==0) {
			dothing.getBlock().setType(Material.getMaterial(ee.getMetadata("fallsand1").get(0).asInt()));
			dothing.getBlock().setData((byte) ee.getMetadata("fallsand2").get(0).asShort());
			ee.getWorld().playSound(dothing, bs.getBreakSound(dothing.getBlock().getType()+""), 1, 1);
			fallingsands.remove(ee);
			ee.remove();
		}
		else if(check==1) {
			dothing.getBlock().setType(Material.getMaterial(ee.getPassengers().get(0).getMetadata("fallsand1").get(0).asInt()));
			dothing.getBlock().setData((byte) ee.getPassengers().get(0).getMetadata("fallsand2").get(0).asShort());
			ee.getWorld().playSound(dothing, bs.getBreakSound(dothing.getBlock().getType()+""), 1, 1);
			fallingsands.remove(ee.getPassengers().get(0));
			ee.getPassengers().get(0).remove();
			fallingsands.remove(ee);
			ee.remove();
		}
	}
	
	public void dofallingsands() {
		if(!fallingsands.isEmpty()) {
			List<Entity> fallingsands2 = new ArrayList<Entity>(fallingsands);
			for(Entity ee : fallingsands2) {
				if(ee!=null) {
					if(ee.isOnGround()==true||(ee.getLocation().getBlock().getType()==Material.STATIONARY_LAVA||ee.getLocation().getBlock().getType()==Material.LAVA||ee.getLocation().getBlock().getType()==Material.STATIONARY_WATER||ee.getLocation().getBlock().getType()==Material.WATER)) {
						if(ee instanceof ArmorStand) {
							placeFallingSand(ee, 0);
						}
						else {
							Vector v = null;
							if(ee.getPassengers().size()>0) {
							if(ee.getPassengers().get(0).getMetadata("fallsand1").size()>0) {
							if(dur.softs.contains("" + Material.getMaterial(ee.getPassengers().get(0).getMetadata("fallsand1").get(0).asInt()))) {
							v = checkSides(ee.getLocation().getBlock().getLocation());
							}
							}
							if(v==null) {
							if(ee.getPassengers()!=null) {
								if(ee.getPassengers().size()>0) {
									placeFallingSand(ee, 1);
								}
							}
							}
							else {
								ee.setVelocity(v);
							}
							}
							else {
								ee.getPassengers().clear();
								fallingsands.remove(ee);
								ee.remove();
							}
						}
					}
				}
			}
		}
	}
	
	public Vector checkSides(Location l) {
		Vector v = null;
		List<BlockFace> sidetypes = new ArrayList<BlockFace>();
		BlockFace[] sides = {BlockFace.WEST,BlockFace.SOUTH,BlockFace.NORTH,BlockFace.EAST};
		for(int sidesR = 0; sidesR < sides.length; sidesR++) {
			Block b1 = l.clone().getBlock().getRelative(sides[sidesR]);
			Block b2 = l.clone().subtract(0,1,0).getBlock().getRelative(sides[sidesR]);
			if(b1.getType()==Material.AIR && b2.getType()==Material.AIR) {
				sidetypes.add(sides[sidesR]);
			}
		}
		if(sidetypes.size()>0) {
		BlockFace choosen = sidetypes.get(randor.nextInt(sidetypes.size()));
		if(choosen == BlockFace.WEST) {
			v = new Vector(-.3, 0, 0);
		}
		else if(choosen == BlockFace.NORTH) {
			v = new Vector(0, 0, -.3);
		}
		else if(choosen == BlockFace.SOUTH) {
			v = new Vector(0, 0, .3);
		}
		else if(choosen == BlockFace.EAST) {
			v = new Vector(.3, 0, 0);
		}
		return v;
		}
		else {
			return null;
		}
	}
	
	public void deleteArmor() {
		if(!armors.isEmpty()) {
			HashMap<Entity, Long> armors2 = new HashMap<Entity, Long>(armors);
			for(Entity ee : armors2.keySet()) {
				if(armors2.get(ee)<System.currentTimeMillis()) {
					armors.remove(ee);
					ee.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void setWorld(PlayerJoinEvent event) {
		if(wor==null) {
		wor = event.getPlayer().getWorld();
		}
		else {
			return;
		}
	}
	
	@SuppressWarnings("unused")
	public void betterEffectLooper() {
		if(!effectEnts.isEmpty()) {
		List<Entity> tempEntss = new ArrayList<Entity>(effectEnts);
		for(Entity e : tempEntss) {
			LivingEntity e2 = (LivingEntity) e;
			if(true == false) {//!worlds.contains(e2.getWorld().getName())) {
				effectEnts.remove(e);
			}
			else {
			if(e2 != null) {
				if(!e2.isDead()) {
					if(e2 instanceof Player) {
						Location feet = e2.getLocation().subtract(0, 1, 0);
						Location leg = e2.getLocation();
						Location head = e2.getLocation().add(0, 1, 0);
						int temp1 = 0; int temp2 = 0; int temp3 = 0;
						boolean hasTemp = false;
						if(feet.getBlock().hasMetadata("T")) {
							hasTemp = true;
							if(!feet.getBlock().getMetadata("T").isEmpty()) {
								temp1 = feet.getBlock().getMetadata("T").get(0).asInt();
							}
						}
						if(leg.getBlock().hasMetadata("T")) {
							hasTemp = true;
							if(!leg.getBlock().getMetadata("T").isEmpty()) {
								temp2 = leg.getBlock().getMetadata("T").get(0).asInt();
							}
						}
						if(head.getBlock().hasMetadata("T")) {
							hasTemp = true;
							if(!head.getBlock().getMetadata("T").isEmpty()) {
								temp3 = head.getBlock().getMetadata("T").get(0).asInt();
							}
						}
						if(hasTemp == false) {
							effectEnts.remove(e);
						}
						else {
							runTemperature(((Player) e2), Math.max(Math.max(temp1, temp2), temp3));
						}
					}
					else {
						effectEnts.remove(e);	
					}
					}
				else {
					effectEnts.remove(e);	
				}
			}
			else {
				effectEnts.remove(e);	
			}
			}
			}
		}
		}
	
	public void runFireTemp() {
		if(config.getBoolean("Enable Temperature ")) {
		if(!fires.isEmpty()) {
			List<Block> fires2 = new ArrayList<Block>(fires);
			for(Block b : fires2) {
				//removal
				if(b.getType()!=Material.FIRE) {
					fires.remove(b);
					if(b.hasMetadata("T")) {
						if(b.getMetadata("T").size()>0) {
							tempers.put(b, b.getMetadata("T").get(0).asInt());
						}
						else {
							tempers.put(b, 2000);
						}
					}
					else {
						tempers.put(b, 2000);
					}
					BlockFace[] sides = {BlockFace.DOWN,BlockFace.WEST,BlockFace.UP,BlockFace.SOUTH,BlockFace.NORTH,BlockFace.EAST};
					for(int sidesR = 0; sidesR < sides.length; sidesR++) {
						Block b2 = b.getRelative(sides[sidesR]);
						if(b2.getType()!=Material.FIRE) {
							if(fires.contains(b2)) {
								fires.remove(b2);
							}
							if(firesT.contains(b2)) {
								firesT.remove(b2);
							}
						if(b2.hasMetadata("T")) {
							if(b2.getMetadata("T").size()>0) {
								tempers.put(b2, b2.getMetadata("T").get(0).asInt());
							}
							else {
								tempers.put(b2, 2000);
							}
						}
						else {
							tempers.put(b2, 2000);
						}
						}
					}
				}
				//
				else {
				BlockFace[] sides = {BlockFace.DOWN,BlockFace.WEST,BlockFace.UP,BlockFace.SOUTH,BlockFace.NORTH,BlockFace.EAST,BlockFace.SELF};
				for(int sidesR = 0; sidesR < sides.length; sidesR++) {
					Block b2 = b.getRelative(sides[sidesR]);
					if(sides[sidesR]!=BlockFace.SELF) {
						if(!firesT.contains(b2)) {
							firesT.add(b2);
						}
					}
					if(b2.hasMetadata("T")) {
						if(b2.getMetadata("T").size()>0) {
							if(b2.getMetadata("T").get(0).asInt()>=2200) {
								
							}
							else {
							b2.setMetadata("T", new FixedMetadataValue(this, b2.getMetadata("T").get(0).asInt()+300));
							}
						}
						else {
							b2.setMetadata("T", new FixedMetadataValue(this, 300));
						}
					}
					else {
						b2.setMetadata("T", new FixedMetadataValue(this, 300));
					}
				}
			}
			}
		}
		}
	}
	
	public void runTemperature(Player p, int temp) {
		if(temp<125) {
			if(randor.nextInt(20)==0) {
				p.damage(1);
			}
		}
		else if(temp<256) {
			if(randor.nextInt(16)==0) {
				p.damage(1);
			}
		}
		else if(temp<526) {
			if(randor.nextInt(15)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(40);
				}
			}
		}
		else if(temp<1027) {
			if(randor.nextInt(10)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(80);
				}
			}
		}
		else if(temp<2546) {
			if(randor.nextInt(7)==0) {
				p.damage(2);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(160);
				}
			}
		}
		else if(temp<5678) {
			if(randor.nextInt(4)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(360);
				}
			}
		}
		else {
			if(randor.nextInt(2)==0) {
				p.damage(4);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(1200);
				}
			}
		}
	}

	public void runTemperatureE(LivingEntity p, int temp) {
		if(temp<125) {
			if(randor.nextInt(20)==0) {
				p.damage(1);
			}
		}
		else if(temp<256) {
			if(randor.nextInt(16)==0) {
				p.damage(1);
			}
		}
		else if(temp<526) {
			if(randor.nextInt(15)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(40);
				}
			}
		}
		else if(temp<1027) {
			if(randor.nextInt(10)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(80);
				}
			}
		}
		else if(temp<2546) {
			if(randor.nextInt(7)==0) {
				p.damage(2);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(160);
				}
			}
		}
		else if(temp<5678) {
			if(randor.nextInt(4)==0) {
				p.damage(1);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(360);
				}
			}
		}
		else {
			if(randor.nextInt(2)==0) {
				p.damage(4);
				if(p.getFireTicks()<=0) {
					p.setFireTicks(1200);
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (command.getName().equalsIgnoreCase("power")) {
		if (sender instanceof Player) {
				if(args!=null&&args.length>0) {
					radius = Integer.parseInt(args[0]);
					if(powers.containsKey((Player) sender)) {
						powers.remove(((Player) sender));
					}
					else {
						powers.put(((Player) sender), Integer.parseInt(args[0]));
					}
				}
		}
	}
	if (command.getName().equalsIgnoreCase("water")) {
		if (sender instanceof Player) {
			doWaterCommand((Player) sender, args[0]);
		}
	}
	if (command.getName().equalsIgnoreCase("ssand")) {
		if (sender instanceof Player) {
				if(args!=null&&args.length>2) {
					typess = Integer.parseInt(args[0]);
					datass = (short) Integer.parseInt(args[1]);
					int remove = Integer.parseInt(args[2]);
					if(remove == 0) {
						wand.remove(((Player) sender));
					}
					else {
						if(wand.contains(((Player) sender))) {
							
						}
						else {
							wand.add(((Player) sender));
						}
					}
				}
		}
	}
	if (command.getName().equalsIgnoreCase("dorealdrops")) {
		if (sender instanceof Player) {
			doRealisticDrops = !doRealisticDrops;
			return true;
		}
	}
	if (command.getName().equalsIgnoreCase("dodyntemp")) {
		if (sender instanceof Player) {
			dyTemp = !dyTemp;
			return true;
		}
	}
	return true;
	}
	
	public void removeTemp() {
		if(!tempers.isEmpty()) {
			HashMap<Block, Integer> temps = new HashMap<Block, Integer>(tempers);
			for(Block b : temps.keySet()) {
				int newTemp = temps.get(b)/4;
				if(newTemp<=0) {
					b.removeMetadata("T", this);
					tempers.remove(b);
				}
				else {
					tempers.put(b, newTemp);
					b.setMetadata("T", new FixedMetadataValue(this, newTemp));
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		if(config.getBoolean("Enable Enhanced Fires ")) {
		if(!e.isCancelled()) {
			if(config.getBoolean("Enable Netherrack ")==false) {
				if(e.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType()==Material.NETHERRACK) {
					return;
				}
				else if(e.getBlock().getType()==Material.NETHERRACK) {
					return;
				}
			}
			if(!fires.contains(e.getBlock())) {
				if(tempers.containsKey(e.getBlock())) {
					tempers.remove(e.getBlock());
				}
				if(firesT.contains(e.getBlock())) {
					firesT.remove(e.getBlock());
				}
				fires.add(e.getBlock());
			}
		}
		}
	}
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.getPlayer().getLocation().getBlock().hasMetadata("T")) {
		if(event.getPlayer().getLocation().getBlock().getMetadata("T").get(0).asInt()>=30) {
			event.getPlayer().damage(1);
		}
		}
	}
	
	public void doGasEffects() {
		for(gasManager gm : gsM) {
			List<Location> gsLocations = new ArrayList<Location>(gm.getLocations());
			if(!gsLocations.isEmpty()) {
				for(Location l : gsLocations) {
					for(Entity e : l.getWorld().getNearbyEntities(l, 1, 1, 1)) {
						if(e instanceof LivingEntity) {
						if(gm.name.equals("smoke")) {
								((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 200));
								((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0));
						}
						else if(gm.name.equals("steam")) {
							if(randor.nextInt(20)==0) {
							((LivingEntity) e).damage(1);
							}
						}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBurn(BlockBurnEvent e) {
		if(config.getBoolean("Enable Ash ")) {
		if((e.getBlock().getType()+"").toLowerCase().contains("wood")||(e.getBlock().getType()+"").toLowerCase().contains("log")) {
			e.setCancelled(true);
			e.getBlock().setType(Material.CONCRETE_POWDER);
			e.getBlock().setData((byte) 8);
		}
		else if((e.getBlock().getType()+"").toLowerCase().contains("leave")) {
			if(randor.nextInt(7)==0) {
				e.setCancelled(true);
				e.getBlock().setType(Material.CONCRETE_POWDER);
				e.getBlock().setData((byte) 8);
			}
		}
		}
		if(config.getBoolean("Enable Fire Smoke ")) {
		gsM.get(0).addGasLocation(e.getBlock().getLocation().add(0, 1, 0), 1);
		}
	}
	
	public void tempBlockChanges(Block b, int temp) {
		if(b.getType()==Material.FIRE&&temp>600) {
			b.getWorld().spawnParticle(Particle.FLAME, b.getLocation().add(0.5, 0.5, 0.5), 2, .5, .5, .5, 0.06);
		}
		if(temp>1000&&temp<1600) {
			if(randor.nextInt(20)==0) {
			tempAbove1000(b);
			}
		}
		else if(temp>1600&&temp<2000) {
			if(randor.nextInt(20)==0) {
			tempAbove1600(b);
			}
		}
		else if(temp>2000) {
			if(randor.nextInt(20)==0) {
			tempAbove2000(b);
			}
		}
	}
	
	public void tempAbove1000(Block b) {
		Location loc = b.getLocation();
		int radius = 2;
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for(int x = cx - radius; x <= cx + radius; x++){
		for (int z = cz - radius; z <= cz + radius; z++){
		for(int y = (cy - radius); y < (cy + radius); y++){
		double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

			if(dist < radius * radius){
				Location l = new Location(loc.getWorld(), x, y, z);
				if(l.getBlock().getType()==Material.GRASS||l.getBlock().getType()==Material.DIRT) {
				if(randor.nextInt(10)==0) {
					int decided = randor.nextInt(3);
					if(decided==0) {
					l.getBlock().setType(Material.SAND);
					}
					else if(decided==1) {
						l.getBlock().setType(Material.DIRT);
						l.getBlock().setData((byte) 1);
						}
					else if(decided==2) {
						l.getBlock().setType(Material.DIRT);
						l.getBlock().setData((byte) 2);
						}
				}
				}
				else if(l.getBlock().getType()==Material.STATIONARY_WATER) {
					if(randor.nextInt(10)==0) {
					l.getBlock().setType(Material.AIR);
					gsM.get(1).addGasLocation(l.add(0, 1, 0), 1);
					}
				}
			}
		}
		}
		}
	}
	
	public void tempAbove1600(Block b) {
		Location loc = b.getLocation();
		int radius = 3;
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for(int x = cx - radius; x <= cx + radius; x++){
		for (int z = cz - radius; z <= cz + radius; z++){
		for(int y = (cy - radius); y < (cy + radius); y++){
		double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

			if(dist < radius * radius){
				Location l = new Location(loc.getWorld(), x, y, z);
				if(l.getBlock().getType()==Material.GRASS||l.getBlock().getType()==Material.DIRT) {
				if(randor.nextInt(10)==0) {
					int decided = randor.nextInt(3);
					if(decided==0) {
					l.getBlock().setType(Material.SAND);
					}
					else if(decided==1) {
						l.getBlock().setType(Material.DIRT);
						l.getBlock().setData((byte) 1);
						}
					else if(decided==2) {
						l.getBlock().setType(Material.DIRT);
						l.getBlock().setData((byte) 2);
						}
				}
				}
				else if((l.getBlock().getType()+"").toLowerCase().contains("stone")&&(!(l.getBlock().getType()+"").toLowerCase().contains("red"))) {
					if(randor.nextInt(10)==0) {
						if(l.getBlock().getType()==Material.COBBLESTONE) {
							if(randor.nextBoolean()==true) {
								l.getBlock().setType(Material.STONE);
							}
						}
						else {
							l.getBlock().setType(Material.MAGMA);
						}
					}
				}
				else if((l.getBlock().getType()+"").toLowerCase().contains("leave")||(l.getBlock().getType()+"").toLowerCase().contains("wood")||(l.getBlock().getType()+"").toLowerCase().contains("log")) {
					if(doRealisticSpreading == true) {
					if(randor.nextInt(20)==0) {
					l.getBlock().setType(Material.FIRE);
					}
					}
				}
				else if(l.getBlock().getType()==Material.STATIONARY_WATER) {
					if(randor.nextInt(10)==0) {
					l.getBlock().setType(Material.AIR);
					gsM.get(1).addGasLocation(l.add(0, 1, 0), 1);
					}
				}
			}
		}
		}
		}
	}

	public void tempAbove2000(Block b) {
		Location loc = b.getLocation();
		int radius = 3;
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for(int x = cx - radius; x <= cx + radius; x++){
		for (int z = cz - radius; z <= cz + radius; z++){
		for(int y = (cy - radius); y < (cy + radius); y++){
		double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

			if(dist < radius * radius){
				Location l = new Location(loc.getWorld(), x, y, z);
				if(((l.getBlock().getType()+"").toLowerCase().contains("stone")||l.getBlock().getType()==Material.MAGMA)&&(!(l.getBlock().getType()+"").toLowerCase().contains("red"))) {
					if(randor.nextInt(10)==0) {
						if(randor.nextInt(5)==0) {
						l.getBlock().setType(Material.STATIONARY_LAVA);
						}
						else {
							l.getBlock().setType(Material.MAGMA);
						}
					}
				}
				else if(l.getBlock().getType()==Material.STATIONARY_WATER) {
					if(randor.nextInt(6)==0) {
					l.getBlock().setType(Material.AIR);
					gsM.get(1).addGasLocation(l.add(0, 1, 0), 1);
					}
				}
			}
		}
		}
		}
	}
	
	@EventHandler
	public void onWaterChange(BlockFormEvent e) {
		if(e.isCancelled()) {
			
		}
		else {
			if(config.getBoolean("Enable Steam ")) {
			if((e.getBlock().getType()+"").toLowerCase().contains("lava")||(e.getBlock().getType()+"").toLowerCase().contains("water")) {
					gsM.get(1).addGasLocation(e.getBlock().getLocation().add(0, 2, 0), 1);
			}
			}
		}
	}
	
	public void doDynamicTemperature() {
		if(dyTemp==true) {
			if(!tempers.isEmpty()) {
				HashMap<Block, Integer> temps = new HashMap<Block, Integer>(tempers);
				for(Block b : temps.keySet()) {
					for(Entity e : b.getWorld().getNearbyEntities(b.getLocation(), 2, 2, 2)) {
						if(e instanceof LivingEntity && (!(e instanceof Player))) {
							runTemperatureE((LivingEntity) e, temps.get(b));
						}
					}
					if(b.getType()==Material.FIRE&&temps.get(b)>300) {
						b.getWorld().spawnParticle(Particle.FLAME, b.getLocation().add(0.5, 0.5, 0.5), 2, .5, .5, .5, 0.04);
					}
					if(temps.get(b)>1000) {
						if(randor.nextInt(10)==0) {
							Location oldL = b.getLocation().add((randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0));
							oldL.getWorld().spawnParticle(Particle.SMOKE_NORMAL, oldL, 1, 0, 0, 0, 0.01);
						}
						tempBlockChanges(b, temps.get(b));
					}
				}
			}
			if(!fires.isEmpty()) {
				List<Block> temps = new ArrayList<Block>(fires);
				for(Block b : temps) {
					int temp = 0;
					if(b.hasMetadata("T")) {
						if(b.getMetadata("T").size()>0) {
							temp = b.getMetadata("T").get(0).asInt();
						}
					}
					if(temp>0) {
					for(Entity e : b.getWorld().getNearbyEntities(b.getLocation(), 2, 2, 2)) {
						if(e instanceof LivingEntity && (!(e instanceof Player))) {
							runTemperatureE((LivingEntity) e, temp);
						}
					}
					if(b.getType()==Material.FIRE&&temp>300) {
						b.getWorld().spawnParticle(Particle.FLAME, b.getLocation().add(0.5, 0.5, 0.5), 2, .5, .5, .5, 0.04);
					}
					if(temp>1000) {
						if(randor.nextInt(10)==0) {
							Location oldL = b.getLocation().add((randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0));
							oldL.getWorld().spawnParticle(Particle.SMOKE_NORMAL, oldL, 1, 0, 0, 0, 0.01);
						}
						tempBlockChanges(b, temp);
					}
					}
				}
			}
			if(!firesT.isEmpty()) {
				List<Block> temps = new ArrayList<Block>(firesT);
				for(Block b : temps) {
					int temp = 0;
					if(b.hasMetadata("T")) {
						if(b.getMetadata("T").size()>0) {
							temp = b.getMetadata("T").get(0).asInt();
						}
					}
					if(temp>0) {
					for(Entity e : b.getWorld().getNearbyEntities(b.getLocation(), 2, 2, 2)) {
						if(e instanceof LivingEntity && (!(e instanceof Player))) {
							runTemperatureE((LivingEntity) e, temp);
						}
					}
					if(b.getType()==Material.FIRE&&temp>300) {
						b.getWorld().spawnParticle(Particle.FLAME, b.getLocation().add(0.5, 0.5, 0.5), 2, .5, .5, .5, 0.04);
					}
					if(temp>1000) {
						if(randor.nextInt(10)==0) {
							Location oldL = b.getLocation().add((randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0), (randor.nextInt(100)/100.0));
							oldL.getWorld().spawnParticle(Particle.SMOKE_NORMAL, oldL, 1, 0, 0, 0, 0.01);
						}
						tempBlockChanges(b, temp);
					}
					}
				}
			}
		}
	}
	
	public void doBlockStuff(List<Block> bl, int key, int eRange, Location begin) {
		for(Block bls : bl) {
			if(randor.nextInt(8)==0) {
				if(randor.nextBoolean()==true&&randor.nextInt(20)!=0) {
				bls.getWorld().playSound(bls.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				}
				else {
					bls.getWorld().playSound(bls.getLocation(), bs.getBreakSound(bls.getLocation().getBlock().getType()+""), 1, 1);
				}
			}
			if(randor.nextInt(20)==0) {
				if(key>eRange-eRange/5) {
					if(config.getBoolean("Enable Block Physics ")) {
					if(randor.nextInt(2)==0 && bls.getType()!=Material.AIR) {
						FallingBlock fb = bls.getWorld().spawnFallingBlock(bls.getLocation(), bls.getState().getData());
						Entity fb = spawnRealFSand(bls.getLocation(), bls.getTypeId(), bls.getData());
						fb.setVelocity(begin.toVector().subtract(fb.getLocation().toVector()).normalize().multiply(1.2));
						bls.setType(Material.AIR);
						if(bls.hasMetadata("D")) {
							bls.removeMetadata("D", this);
						}
					}
					}
					bls.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, bls.getLocation(), 1);
				}
				else if(key>eRange-eRange/2) {
					bls.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bls.getLocation(), 1);
				}
				else {
					bls.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, bls.getLocation(), 1);
				}
			}
			if(randor.nextInt(20)==0) {
				
			}
			if(bls.getType()==Material.TNT) {
				bls.setType(Material.AIR);
				HashMap<Integer, List<Block>> empty = new HashMap<Integer, List<Block>>();
				iterate(empty, bls.getLocation(), (int) (.25*16)*2, (int) ((.25*16)*(.25*16)*(.25*16))*2, 350);
			}
			if(bls.hasMetadata("D")) {
				bls.removeMetadata("D", this);
			}
			if(randor.nextInt(5)!=0) {
			bls.setType(Material.AIR);
			}
			else {
				bls.breakNaturally();
			}
		}
	}
	
	public int checkRadius() {
		if(radius>120) {
			return radius*2;
		}
		else if(radius>90) {
			return radius*3;
		}
		else if(radius>60) {
			return radius*4;
		}
		else if(radius>16) {
			return radius*radius;
		}
		else {
			return radius*radius*radius;
		}
	}
	
	public void deleteBlocks(final HashMap<Integer, List<Block>> blocks) {
		console.sendMessage("" + blocks.size());
		int counterAdd = 0;
		int counterr = 0;
		int counterr2 = 0;
		for(int key : blocks.keySet()) 
		{
			if(counterAdd>=18) {
				Bukkit.getScheduler().runTaskLater(this,
						() -> doBlockStuff(blocks.get(key)), 18+counterr+counterAdd-9);
				counterr+=18;
				counterAdd+=1;
			}
			else {
				Bukkit.getScheduler().runTaskLater(this,
						() -> doBlockStuff(blocks.get(key)), 0+counterr2);
				counterr2+=5;
			}
		}
	}
	
	public void doBlockCount(int countLeft, HashMap<Integer, List<Block>> blocks, Location loc) {
		if(countLeft<=0) {
			deleteBlocks(blocks);
			return;
		}
		else {
			int ammount = 0;
			if(countLeft<10000) {
				ammount=countLeft;
			}
			else {
				ammount=10000;
			}
			for(int i = 0; i < ammount; i++) {
				final BlockIterator bit = new BlockIterator(wor, loc.toVector(), new Vector(0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random()), 0, radius);
				int blockNum = 1;
				while (bit.hasNext()) {
					final Block next = bit.next();
					if (next != null) {
						if(blocks.containsKey(blockNum)) {
							if(!blocks.get(blockNum).contains(next)) {
							blocks.get(blockNum).add(next);
							}
						}
						else {
							List<Block> blnew = new ArrayList<Block>();
							blnew.add(next);
							blocks.put(blockNum, blnew);
						}
					}
					blockNum++;
				}
			}
			Bukkit.getScheduler().runTaskLater(this,
					() -> doBlockCount(countLeft-10000, blocks, loc), 18);
		}
	}
	
	@EventHandler
	public void onInteract2(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			if(powers.containsKey(event.getPlayer())) {
				Location loc = event.getClickedBlock().getLocation();
				HashMap<Integer, List<Block>> blocks = new HashMap<Integer, List<Block>>();
				iterate(blocks, loc, radius, radius*radius*radius, radius*radius*radius);
			}
		}
		else if(event.getAction()==Action.RIGHT_CLICK_AIR) {
			if(wand.contains(event.getPlayer())) {
				for(int i = 0; i < 5; i++) {
				Entity sand = spawnRealFSand(event.getPlayer().getLocation().add(randor.nextInt(3)*getNegRand(), 2+randor.nextInt(3), randor.nextInt(3)*getNegRand()), typess, datass);
				sand.setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.5));
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().hasMetadata("D")) {
			event.getBlock().removeMetadata("D", this);
		}
		if(config.getBoolean("Enable 3d Drops ")) {
		boolean haslight = false;
		BlockFace[] sides = {BlockFace.DOWN,BlockFace.WEST,BlockFace.UP,BlockFace.SOUTH,BlockFace.NORTH,BlockFace.EAST};
		for(int sidesR = 0; sidesR < sides.length; sidesR++) {
			if(event.getBlock().getRelative(sides[sidesR]).getLightLevel()>=11) {
				haslight = true;
				break;
			}
		}
		int count = randor.nextInt(2)+1;
		for(int ct = 0; ct < count; ct++) {
			Location oldL = event.getBlock().getLocation().add(.2+(randor.nextInt(60)/100.0), .2+(randor.nextInt(60)/100.0), .2+(randor.nextInt(60)/100.0));
			Location newL = new Location(oldL.getWorld(),oldL.getX(),oldL.getY(),oldL.getZ(), randor.nextInt(360),randor.nextInt(360));
			ArmorStand bby = (ArmorStand) event.getBlock().getWorld().spawnEntity(newL, EntityType.ARMOR_STAND);
			bby.setVisible(false);
			bby.setMarker(true);
			bby.setSilent(true);
			bby.setArms(true);
			bby.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 200, false, false));
			ItemStack i = new ItemStack(event.getBlock().getType());
			i.setData(event.getBlock().getState().getData());
			i.setDurability(event.getBlock().getData());
			bby.getEquipment().setItemInMainHand(i);
			bby.setSmall(true);
			bby.setBasePlate(false);
			if(haslight) {
			bby.setFireTicks(999999);
			}
			armors.put(bby, System.currentTimeMillis()+(3*1000));
		}
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().hasMetadata("D")) {
			event.getBlock().removeMetadata("D", this);
		}
	}
	@EventHandler
	public void onPMove(PlayerMoveEvent event) {
		if(config.getBoolean("Enable Temperature ")) {
		if(!tempers.isEmpty()) {
			if(!effectEnts.contains(event.getPlayer())) {
		Player e2 = event.getPlayer();
		Location feet = e2.getLocation().subtract(0, 1, 0);
		Location leg = e2.getLocation();
		Location head = e2.getLocation().add(0, 1, 0);
		int temp1 = 0; int temp2 = 0; int temp3 = 0;
		if(feet.getBlock().hasMetadata("T")) {
			if(!feet.getBlock().getMetadata("T").isEmpty()) {
				temp1 = feet.getBlock().getMetadata("T").get(0).asInt();
			}
		}
		if(leg.getBlock().hasMetadata("T")) {
			if(!leg.getBlock().getMetadata("T").isEmpty()) {
				temp2 = leg.getBlock().getMetadata("T").get(0).asInt();
			}
		}
		if(head.getBlock().hasMetadata("T")) {
			if(!head.getBlock().getMetadata("T").isEmpty()) {
				temp3 = head.getBlock().getMetadata("T").get(0).asInt();
			}
		}
		if(Math.max(Math.max(temp1, temp2), temp3)>32) {
			effectEnts.add(e2);
		}
			}
		}
		}
	}
	
	public void iterate(HashMap<Integer, List<Block>> blocksF, Location locer, int eRange, int ammount, int temperature){
		if(eRange>30 || eRange<0) {
			return;
		}
		HashMap<Integer, List<Block>> blocks = blocksF;
		int times = 1000;
		if(ammount <= 1000) {
			times = ammount;
		}
		ammount = ammount - 1000;
		for(int i = 0; i < times; i++) {
			final BlockIterator bit = new BlockIterator(wor, locer.toVector(), new Vector(0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random()), 0, eRange);
			int blockNum = 1;
			double power = eRange*eRange*eRange/3;
			int temptemp = temperature;
			while (bit.hasNext()) {
				final Block next = bit.next();
				String name = (next.getType()+"").toLowerCase();
				if((name.contains("redstone")&&(!(name.contains("block")||name.contains("ore"))))||name.contains("bedrock")||name.contains("barrier")||name.contains("command")) {
					break;
				}
				HashMap<Double, Integer> newV = runBlockDecision(next, power, locer, temptemp);
				for(Double d : newV.keySet()) {
					power = d;
					temptemp = newV.get(d);
					break;
				}
				if(power>0) {
				if (next != null) {
					if(blocks.containsKey(blockNum)) {
						if(!blocks.get(blockNum).contains(next)) {
						blocks.get(blockNum).add(next);
						}
					}
					else {
						List<Block> blnew = new ArrayList<Block>();
						blnew.add(next);
						blocks.put(blockNum, blnew);
					}
				}
				blockNum++;
				}
				else {
					break;
				}
			}
		}
		if(ammount > 1000) {
			final int am = ammount;
			Bukkit.getScheduler().runTaskLater(this, () -> iterate(blocks, locer, eRange, am, temperature), 40);
		}
		else {
			throwExplosion(locer, eRange, blocks);
		}
	}
 
	public HashMap<Double, Integer> runBlockDecision(Block b, double power, Location begin, int temp) {
		HashMap<Double, Integer> values = new HashMap<Double, Integer>();
		Material m = b.getType();
		double pow2 = power;
		int temp2 = temp;
		if(b.hasMetadata("D")) {
			if(!b.getMetadata("D").isEmpty()) {
			pow2 = pow2 - b.getMetadata("D").get(0).asDouble();
			}
		}
		else {
			pow2 = pow2 - dur.duras.get(m + "");
		}
		if(config.getBoolean("Enable Temperature ")) {
		if(b.hasMetadata("T")) {
			if(!b.getMetadata("T").isEmpty()) {
				int calc = (int) (temp2 / (dur.temp.get(b.getType() + "")/4.0));
				int tempB = b.getMetadata("T").get(0).asInt();
					temp2 = temp2 - dur.temp.get(b.getType() + "");
				if(tempB>=temp2) {
					temp2 = calc;
				}
				else {
					b.setMetadata("T", new FixedMetadataValue(this, temp2));
					tempers.put(b, temp2);
					temp2 = calc;
				}
			}
		}
		else {
			int calc = (int) (temp2 / (dur.temp.get(b.getType() + "")/4.0));
			b.setMetadata("T", new FixedMetadataValue(this, temp2));
			tempers.put(b, temp2);
			temp2 = calc;
		}
		}
		for(Entity e : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
			if(e instanceof LivingEntity) {
				if(pow2>0) {
					if(e instanceof Player) {
						damageReducer.ReducedDamage(((LivingEntity) e), pow2/7.8);
					}
					else {
						((LivingEntity) e).damage(pow2/2);
					}
				}
			}
			if(pow2>0) {
				try {
					e.setVelocity(begin.toVector().subtract(e.getLocation().toVector()).normalize().multiply(-1-(pow2/100)));
				}
				catch(Exception ee) {
					
				}
			}
		}
		if(pow2<=0) {
			if(b.hasMetadata("D")) {
				if(!b.getMetadata("D").isEmpty()) {
				b.setMetadata("D", new FixedMetadataValue(this, b.getMetadata("D").get(0).asDouble() - power));
				if(b.getMetadata("D").get(0).asDouble()<=dur.duras.get(m + "")/2.0) {
					if(dur.converts.containsKey(b.getType() + "")) {
					b.setType(dur.converts.get(b.getType() + ""));
					}
				}
				}
				values.put(pow2, temp2);
				return values;
			}
			else {
				b.setMetadata("D", new FixedMetadataValue(this, dur.duras.get(m + "")-power));
				if(!b.getMetadata("D").isEmpty()) {
				if(b.getMetadata("D").get(0).asDouble()<=dur.duras.get(m + "")/2.0) {
					if(dur.converts.containsKey(b.getType() + "")) {
					b.setType(dur.converts.get(b.getType() + ""));
					}
				}
				}
				values.put(pow2, temp2);
				return values;
			}
		}
		else {
			values.put(pow2, temp2);
			return values;
		}
	}
	
	@EventHandler
	public void onDamaged(EntityDamageEvent event) {
		if(config.getBoolean("Enable Enhanced Explosions ")) {
		if(event.getCause()==DamageCause.BLOCK_EXPLOSION || event.getCause()==DamageCause.ENTITY_EXPLOSION) {
			event.setCancelled(true);
		}
		}
	}
	
	public HashMap<Integer, List<Block>> getBlocks(Location locer, int eRange){
		Location loc = locer;
		HashMap<Integer, List<Block>> blocks = new HashMap<Integer, List<Block>>();
		//block range getter
		int radiuse = eRange;
		int fullRadi = radiuse*radiuse*radiuse;
		if(fullRadi>6000) {
		for(int i = 0; i < radiuse*radiuse*radiuse; i++) {
			final BlockIterator bit = new BlockIterator(wor, loc.toVector(), new Vector(randor.nextInt(360)+1,randor.nextInt(360)+1,randor.nextInt(360)+1), 0, radius);
			final BlockIterator bit = new BlockIterator(wor, loc.toVector(), new Vector(0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random()), 0, radiuse);
			int blockNum = 1;
			while (bit.hasNext()) {
				final Block next = bit.next();
				if (next != null) {
					if(blocks.containsKey(blockNum)) {
						if(!blocks.get(blockNum).contains(next)) {
						blocks.get(blockNum).add(next);
						}
					}
					else {
						List<Block> blnew = new ArrayList<Block>();
						blnew.add(next);
						blocks.put(blockNum, blnew);
					}
				}
				blockNum++;
			}
		}
		}
		return blocks;
	}
	
	public int getNegRand() {
		if(randor.nextBoolean()) {
			return 1;
		}
		else { 
			return -1;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK) {
		Location loc = event.getClickedBlock().getLocation();
		HashMap<Integer, List<Block>> blocks = new HashMap<Integer, List<Block>>();
		//block range getter
		for(int i = 0; i < radius*radius*radius; i++) {
			final BlockIterator bit = new BlockIterator(wor, loc.toVector(), new Vector(randor.nextInt(360)+1,randor.nextInt(360)+1,randor.nextInt(360)+1), 0, radius);
			final BlockIterator bit = new BlockIterator(wor, loc.toVector(), new Vector(0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random(),0.0D + Math.random() - Math.random()), 0, radius);
			int blockNum = 1;
			while (bit.hasNext()) {
				final Block next = bit.next();
				if (next != null) {
					if(blocks.containsKey(blockNum)) {
						if(!blocks.get(blockNum).contains(next)) {
						blocks.get(blockNum).add(next);
						}
					}
					else {
						List<Block> blnew = new ArrayList<Block>();
						blnew.add(next);
						blocks.put(blockNum, blnew);
					}
				}
				blockNum++;
			}
		}
		for(int key : blocks.keySet()) {
			int counterr = 0;
			if(radius>20) {
				Bukkit.getScheduler().runTaskLater(this,
						() -> doBlockStuff(blocks.get(key)), 40+counterr);
				counterr+=41;
			}
			else {
			for(Block bls : blocks.get(key)) {
				bls.setType(Material.AIR);
			}
			}
		}
		
	}
	}
	int cx = loc.getBlockX();
	int cy = loc.getBlockY();
	int cz = loc.getBlockZ();
	for (int x = cx - radius; x <= cx + radius; x++) {
		for (int z = cz - radius; z <= cz + radius; z++) {
			for (int y = (cy - radius); y < (cy + radius); y++) {
				double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

				if (dist < radius * radius) {
					Location l = new Location(loc.getWorld(), x, y, z);
					if(l.getBlock().hasMetadata("T")) {
						l.getBlock().setMetadata("T",  new FixedMetadataValue(this, l.getBlock().getMetadata("T").get(0).asInt()+3));
						if(l.getBlock().getMetadata("T").get(0).asInt()>=30) {
							l.getBlock().setType(Material.AIR);
						}
					}
					else{
						console.sendMessage("reeee");
						l.getBlock().setMetadata("T",  new FixedMetadataValue(this, 3));
					}
				}
			}
		}
	}
	public void throwExplosion(Location locer, int eRange, HashMap<Integer, List<Block>> blocks) {
		locer.getWorld().playSound(locer, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		for(int key : blocks.keySet()) {
			int counterr = 0;
			if(eRange>20) {
				Bukkit.getScheduler().runTaskLater(this,
						() -> doBlockStuff(blocks.get(key), key, eRange, locer), 40+counterr);
				counterr+=41;
			}
			else {
				doBlockStuff(blocks.get(key), key, eRange, locer);
			for(Block bls : blocks.get(key)) {
				if(randor.nextInt(8)==0) {
					locer.getWorld().playSound(bls.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				}
				if(bls.getType()==Material.TNT) {
					bls.setType(Material.AIR);
					HashMap<Integer, List<Block>> empty = new HashMap<Integer, List<Block>>();
					iterate(empty, bls.getLocation(), (int) (.25*16), (int) ((.25*16)*(.25*16)*(.25*16)));
				}
				bls.setType(Material.AIR);
			}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(config.getBoolean("Enable Enhanced Explosions ")) {
		if(!event.isCancelled()) {
			if(event.getYield()<0||event.getYield()>50) {
				
			}
			else {
		if(event.getEntityType()==EntityType.PRIMED_TNT||event.getEntityType()==EntityType.CREEPER) {
			event.setCancelled(true);
			HashMap<Integer, List<Block>> empty = new HashMap<Integer, List<Block>>();
			iterate(empty, event.getLocation(), (int) (event.getYield()*16), (int) ((event.getYield()*16)*(event.getYield()*16)*(event.getYield()*16)), (int) (16*((event.getYield()*16)*(event.getYield()*16)*(event.getYield()*16))));
		}
		}
		}
		}
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if(config.getBoolean("Enable Enhanced Explosions ")) {
		if(!event.isCancelled()) {
			if(event.getYield()<0||event.getYield()>50) {
				
			}
			else {
		event.setCancelled(true);
		HashMap<Integer, List<Block>> empty = new HashMap<Integer, List<Block>>();
		iterate(empty, event.getBlock().getLocation(), (int) (event.getYield()*16), (int) ((event.getYield()*16)*(event.getYield()*16)*(event.getYield()*16)), (int) (16*((event.getYield()*16)*(event.getYield()*16)*(event.getYield()*16))));
			}
		}
		}
	}

	public Entity spawnRealFSand(Location l, int id, short data ) {
		Entity e1 = l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		((ArmorStand) e1).setVisible(false);
		e1.setMetadata("fallsand1", new FixedMetadataValue(this, id));
		e1.setMetadata("fallsand2", new FixedMetadataValue(this, data));
		e1.setSilent(true);
		((ArmorStand) e1).setCollidable(true);
		((ArmorStand) e1).setInvulnerable(true);
		MiscDisguise mob = new MiscDisguise(DisguiseType.FALLING_BLOCK);
		FallingBlockWatcher watcher = (FallingBlockWatcher) mob.getWatcher();
		watcher.setBlock(new ItemStack(id, 1, data));
		DisguiseAPI.disguiseEntity(e1, mob);
		fallingsands.add(e1);
		Silverfish e2 = (Silverfish) l.getWorld().spawnEntity(l, EntityType.SILVERFISH);
		e2.addPassenger(e1);
		e2.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 200, false, false));
		e2.setSilent(true);
		e2.setCollidable(false);
		e2.setInvulnerable(true);
		e2.setMetadata("fallsand", new FixedMetadataValue(this, 0));
		fallingsands.add(e2);
		return e2;
	}
	
	void sendPacket(Player p, Object packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
	    Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
	    Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
	    plrConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(plrConnection, packet);
	}

	Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
	    return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + nmsClassName);
	}
	
	@EventHandler
	public void onRedstone(BlockRedstoneEvent e) {
		if(config.getBoolean("Enable Enhanced Explosions ")) {
		Block b = e.getBlock().getRelative(BlockFace.DOWN);
		if(b.hasMetadata("D")) {
			if(b.getMetadata("D").size()>0) {
				int ammount = b.getMetadata("D").get(0).asInt() + 30;
				b.setMetadata("D", new FixedMetadataValue(this, ammount));
			}
			else {
				b.setMetadata("D", new FixedMetadataValue(this, 30));
			}
		}
		else {
			b.setMetadata("D", new FixedMetadataValue(this, 30));
		}
		}
	}
	
	//block gravity
	
	public boolean checkSimilar(Location l1, Location l2) {
		boolean isSim = false;
		if((((int) l1.getX())==((int) l2.getX()))&&(((int) l1.getY())==((int) l2.getY()))&&(((int) l1.getZ())==((int) l2.getZ()))) {
			isSim = true;
		}
		return isSim;
	}
	
	@EventHandler
	public void onBlockBreakedG(BlockBreakEvent event) {
		if(config.getBoolean("Enable Block Physics ")) {
		if(!event.isCancelled()) {
			if(!testGravity(event.getBlock().getLocation(), 0, 0)) {
				Location loc = event.getBlock().getLocation();
				int radius = 4;
				int cx = loc.getBlockX();
				int cy = loc.getBlockY();
				int cz = loc.getBlockZ();
				for(int x = cx - radius; x <= cx + radius; x++){
				for (int z = cz - radius; z <= cz + radius; z++){
				for(int y = (cy - radius); y < (cy + radius); y++){
				double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));

					if(dist < radius * radius){
						Location l = new Location(loc.getWorld(), x, y, z);
						if(l.getBlock().getType()!=Material.AIR&&(!checkSimilar(l, loc))) {
							if(!testGravity(l, 0, 1)) {
								spawnRealFSand(l.add(0.5, 0, 0.5), l.getBlock().getTypeId(), l.getBlock().getData());
								l.getBlock().getLocation().getBlock().setType(Material.AIR);
							}
						}
					}
				}
				}
				}
			}
		}
		}
	}
	
	@EventHandler
	public void onBlockPlacedG(BlockPlaceEvent event) {
		if(config.getBoolean("Enable Block Physics ")) {
		if(!event.isCancelled()) {
			if(!testGravity(event.getBlock().getLocation(), 0, 1)) {
				spawnRealFSand(event.getBlock().getLocation().add(0.5, 0, 0.5), event.getBlock().getTypeId(), event.getBlock().getData());
				event.getBlock().getLocation().getBlock().setType(Material.AIR);
			}
		}
		}
	}
	
	public boolean checkDowns(Location l, int safeAm) {
		boolean isTrue = true;
		for(int i = 0; i < safeAm; i++) {
			if(l.getBlock().getType()==Material.AIR) {
				isTrue = false;
				break;
			}
			l.subtract(0, 1, 0);
		}
		return isTrue;
	}
	
	public boolean testGravity(Location l, int itercount, int type) {
		boolean isSafe = false;
		int lengthSafety = dur.lengths.get("" + l.getBlock().getType());
		if(itercount >= 4 || lengthSafety==25) {
			return true;
		}
		Location checkUp = l.clone();
		boolean hasSupport = true;
		boolean hasSafety = false;
		boolean hasAir = false;
		for(int i = 0; i < lengthSafety+1; i++) {
			if(checkUp.add(0, 1, 0).getBlock().getType()==Material.AIR) {
				hasAir = true;
				break;
			}
		}
		Location checkDown = l.clone();
		for(int i = 0; i < lengthSafety; i++) {
			if(checkDown.subtract(0, 1, 0).getBlock().getType()==Material.AIR&&(!(testGravity(checkDown.clone().add(0, 1, 0), itercount+1, 0)))) {
				isSafe = false;
				hasSupport = false;
				break;
			}
		}
		if(hasSupport==true) {
			isSafe = true;
		}
		if(isSafe == true) {
			if(l.clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR) {
				isSafe = false;
			}
		}
		for(int i = 0; i < 4; i++) {
			Location checkSide = l.clone();
			int checkLength = 0;
			int checkDownLength = 0;
			for(int i2 = 0; i2 < lengthSafety; i2++) {
				if(i==0) {
					Location l2 = checkSide.subtract(0, 0, 1);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							hasSafety = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==1) {
					Location l2 = checkSide.add(0, 0, 1);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							hasSafety = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==2) {
					Location l2 = checkSide.subtract(1, 0, 0);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							hasSafety = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==3) {
					Location l2 = checkSide.add(1, 0, 0);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							hasSafety = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
			}
		}
		if(itercount==0&&(hasAir == true && hasSafety == false && hasSupport == false && isSafe == false)) {
			makeFallUp(l, 4, type);
			return true;
		}
		else {
			return isSafe;
		}
	}
	
	public void makeFallUp(Location l, int fallsize, int type) {
		if(type == 1) {
		spawnRealFSand(l.getBlock().getLocation().clone().add(0.5, 0, 0.5), l.getBlock().getTypeId(), l.getBlock().getData());
		l.getBlock().getLocation().getBlock().setType(Material.AIR);
		}
		Location l1 = l.clone();
		for(int lcount = 0; lcount < fallsize; lcount++) {
			Location l1temp = l1.add(0, 1, 0);
			if(l1temp.getBlock().getType()!=Material.AIR) {
				spawnRealFSand(l1temp.getBlock().getLocation().add(0.5, 0, 0.5), l1temp.getBlock().getTypeId(), l1temp.getBlock().getData());
				l1temp.getBlock().getLocation().getBlock().setType(Material.AIR);
			}
			else {
				break;
			}
		}
		Location l2 = l.clone();
		for(int lcount = 0; lcount < fallsize; lcount++) {
			Location l2temp = l2.subtract(0, 1, 0);
			if(l2temp.getBlock().getType()!=Material.AIR) {
				spawnRealFSand(l2temp.getBlock().getLocation().add(0.5, 0, 0.5), l2temp.getBlock().getTypeId(), l2temp.getBlock().getData());
				l2temp.getBlock().getLocation().getBlock().setType(Material.AIR);
			}
			else {
				break;
			}
		}
	}
	
	//water shite
	
	public void doWaterCommand(Player p, String a) {
		p.getLocation().getBlock().setType(Material.WATER);
		p.getLocation().getBlock().setData(Byte.parseByte(a));
		doWaterPhysics = !doWaterPhysics;
	}
	
	@EventHandler
	public void waterspread(BlockFromToEvent e) {
		if(doWaterPhysics == true) {
			if(e.getBlock().getType()==Material.STATIONARY_WATER||e.getBlock().getType()==Material.WATER) {
				if(e.getBlock().getType()==Material.STATIONARY_WATER) {
					e.getBlock().setType(Material.WATER);
					e.getBlock().setData((byte) 1);
					e.getToBlock().setType(Material.WATER);
					e.getToBlock().setData((byte) (e.getBlock().getData()+1));
				}
				else {
				if(e.getBlock().getData()!=7) {
					e.getToBlock().setType(Material.WATER);
					e.getToBlock().setData((byte) 7);
				}}
				e.getBlock().setType(Material.AIR);
				e.getToBlock().setType(Material.AIR);
				gsM.get(2).addGasLocation(e.getBlock().getLocation(), 1);
				
			}
		}
	}
	
	public boolean testGravity(Location l) {
		boolean isSafe = true;
		boolean isup = false;
		int lengthSafety = 4;
		int downAmmount = 0;
		Location checkDown = l.clone();
		if(isSafe == true) {
		for(int i = 0; i < lengthSafety; i++) {
			if(checkDown.subtract(0, 1, 0).getBlock().getType()==Material.AIR) {
				isSafe = false;
				if(downAmmount==0) {
					isup = true;
				}
				break;
			}
			downAmmount++;
		}}
		if(isSafe==false) {
			if(l.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
			if(testGravity(l.clone().subtract(0, 1, 0))) {
				isSafe = true;
			}
			}
		}
		if(isSafe==false) {
		for(int i = 0; i < 4; i++) {
			Location checkSide = l.clone();
			int checkLength = 0;
			int checkDownLength = 0;
			for(int i2 = 0; i2 < lengthSafety; i2++) {
				if(i==0) {
					Location l2 = checkSide.subtract(0, 0, 1);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==1) {
					Location l2 = checkSide.add(0, 0, 1);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==2) {
					Location l2 = checkSide.subtract(1, 0, 0);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
				if(i==3) {
					Location l2 = checkSide.add(1, 0, 0);
					if(l2.getBlock().getType()==Material.AIR) {
						break;
					}
					if(l2.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR) {
						if(checkDowns(l2.subtract(0, 1, 0), checkLength)) {
							isSafe = true;
							break;
						}
					}
					else {
						checkLength++;
					}
				}
			}
		}
		}
		return isSafe;
	}
	
}
