package forj.elementcombating.impl;

import forj.elementcombating.element.ElementEffect;
import net.minecraft.entity.LivingEntity;

public class ElementDamageInstance {
    public ElementEffect effect;
    int level,duration;
    float damage;
    public ElementDamageInstance(ElementEffect effect,int level,int duration,float damage){
        this.effect=effect;
        this.level=level;
        this.duration=duration;
        this.damage=damage;
    }

    public void damage(LivingEntity target){
        effect.attack(target,target,level,duration,damage);
    }

    public void damage(LivingEntity target,LivingEntity attacker){
        effect.attack(target,attacker,level,duration,damage);
    }

}
