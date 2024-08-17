# Element Combating2

*English translated from Chinese by Fitten Code

**注意：该模组目前处于开发阶段,功能暂不完整,并且可能存在一些bug,请谨慎使用!**

**Note: This mod is currently in development, and the functionality is not complete, and there may be some bugs, please use with caution!**

## 简介 Introduction

这是一个Minecraft Fabric模组，它可以让你和其他生物在游戏中使用元素战斗。

This is a Minecraft Fabric mod that allows you to fight other creatures with elements.

## 前置 Dependencies

- Fabric API
- Gecko Lib

## 功能 Features

- 元素: 火、水、雷电、植物、潜声、灵魂、虚空、岩石、风
- Elements: Fire, Water, Electricity, Plant, Sculk, Soul, Void, Stone, Wind


- 元素能力
    - 物品技能: 玩家可用的附加在剑上的元素能力
    - 实体技能: 其他生物可用的附加在生物上的元素能力
    - 元素爆发: 玩家和其他生物可用的需要充能的元素能力
- Elemental Abilities
    - Item Skills: (Player only)Elemental abilities that can be added to swords
    - Entity Skills: (mob only)Elemental abilities that are added to other creatures
    - Elemental Burst: Elemental abilities that require charging


- 元素反应: 多种元素附着于同一生物时会发生反应,反应时会消除反应的元素并造成特殊效果或伤害
- Elemental Reactions: When multiple elements are attached to the same creature, reactions occur, and the element that
  is reacted with is eliminated and a special effect or damage is inflicted.


- 冷却时间与充能
    - 施放元素能力后,能力对应的元素会进入冷却,期间无法再次施放
    - 施放元素能力后,不同的能力会以不同的方式为元素爆发充能
- Cooldown and Charging
    - After a player casts an elemental ability, the corresponding element will enter cooldown, and the ability cannot be cast again
      during this time.
    - After a player casts an elemental ability, the elemental burst of different abilities will be charged in different ways.


- 元素护盾
  - 某些元素反应会产生元素结晶,反应的攻击者接触它后可以获得元素护盾
  - 护盾具有耐久与持续时间,当护盾被攻击时,会消耗等同于伤害量的耐久
  - 如果护盾元素与攻击元素相同,则护盾会完全抵挡攻击,并且不消耗耐久
  - 如果护盾元素与攻击元素能发生反应,则护盾会损失双倍的耐久
  - 护盾可以抵御大部分物理伤害和元素伤害,但当伤害超过护盾的耐久时,剩余伤害仍然会造成伤害
- Elemental Shields
  - Certain elemental reactions produce elemental crystals, which grant an elemental shield to the attacker upon contact
  - The shield has durability and a duration; when the shield is attacked, it consumes durability equivalent to the amount of damage
  - If the shield's element is the same as the attacking element, the shield completely blocks the attack without consuming durability
  - If the shield's element reacts with the attacking element, the shield loses double the durability
  - The shield can resist most physical and elemental damage, but when the damage exceeds the shield's durability, the remaining damage still causes harm


- 元素宝石与元素升级
  - 玩家击杀怪物时,有概率掉落元素水晶,1个绿宝石与8个元素水晶可以在工作台中合成1级元素宝石
  - 在工作台中,9个元素宝石可以合成更高1级的元素宝石
  - 在铁砧中,元素宝石可以为剑附加或升级元素技能,附加只能使用1级宝石,升级只能使用剑的等级+1的宝石
  - 主手右键元素宝石时,如果宝石等级=元素爆发等级+1,则爆发升级;如果宝石等级=爆发等级-1,则清除元素爆发
  - 清除元素爆发后,主手右键1级元素宝石,可以获取随机的元素爆发
- Elemental Gems and Elemental Upgrades
  - When a player kills a mob, there is a chance to drop elemental crystals, 1 emerald and 8 elemental crystals can be
    crafted into a 1-level elemental gem in the crafting table.
  - In the crafting table, 9 elemental gems can be used to craft a higher level elemental gem.
  - In the anvil, elemental gems can be used to add or upgrade elemental skills to swords, only 1-level gems can be used for
    attaching, and only the gems that level equals to the level of swords +1 can be used for upgrading.
  - When the player right-clicks an elemental gem with the main hand, if the gem level equals to the level of elemental burst
    +1, then the elemental burst is upgraded; if the gem level equals to the level of elemental burst -1, then the elemental
    burst is cleared.
  - After clearing the elemental burst, the player can right-click a 1-level elemental gem to get a random elemental burst.


- 能力信息显示
  - 物品技能的元素,等级与攻击模式会显示在物品名称下方
  - 元素爆发的元素,等级与攻击模式会显示在背包GUI的左上角
- Ability Information Display
  - The element, level, and attack mode of item skills will be displayed below the item name.
  - The element, level, and attack mode of elemental burst will be displayed in the upper left corner of the inventory GUI.


- 命令
  - /give-attribute \[type] \<mode> 为执行者主手中的剑附加元素技能,type为元素类型,mode为攻击模式,不填写时默认随机
  - /element-attack \<targets> \<type> 使targets受到来自执行者的元素攻击,type为元素类型,伤害固定为1,元素附着时间固定为5秒
  - /element-charge \<targets> \<type> \<amount> 为targets的元素爆发充能,type为元素类型,amount为充能量,如果targets能量已满,则命令无效
- Commands
  - /give-attribute \[type] \<mode> Adds an elemental skill to the sword in the player's main hand, type is the element type, and mode is the attack mode, if not filled in, it will be randomly selected.
  - /element-attack \<targets> \<type> Inflicts an elemental attack on targets from the player, type is the element type, the damage is fixed at 1, and the elemental attachment time is fixed at 5 seconds.
  - /element-charge \<targets> \<type> \<amount> Charges the elemental burst of targets, type is the element type, and amount is the charging amount. If the targets' energy is full, the command will be invalid.
