package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.litiengine.abilities.AbilityExecution;
import de.gurkenlabs.litiengine.abilities.OffensiveAbility;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Stomp", cooldown = 10000, value = 5, impact = 100, impactAngle = 360, duration = 300, multiTarget = true)
public class Stomp extends OffensiveAbility {

	public Stomp(Creature executor) {
		super(executor);

		this.addEffect(new HitEffect(this));
		this.addEffect(new ScreenShakeEffect(this, 2, 200));
	}

	@Override
	public AbilityExecution cast() {
		return super.cast();
	}
}
