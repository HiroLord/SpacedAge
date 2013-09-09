public class Weapon{
	private int type;
	private int ammo;
	private boolean automatic;
	private String name;
	private int recoil;

	public Weapon(int type, int ammo){
		this.type = type;
		this.ammo = ammo;
		automatic = false;
		if (type == 1){
			automatic = true;
			name = "Machinegun";
			recoil = 5;
		}
	}

	public Weapon(int type){
		this(type,1);
		if (type == 1)
			ammo = 1000;
	}

	public int getPower(){
		return 5;
	}

	public int getRecoil(){
		return recoil;
	}

	public String getName(){
		return name;
	}
}