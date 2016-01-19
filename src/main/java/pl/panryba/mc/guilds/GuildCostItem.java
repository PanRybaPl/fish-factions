/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 *
 * @author PanRyba.pl
 */
public class GuildCostItem {

    static GuildCostItem deserialize(Map<String, Object> dataMap) {
        Material material = Material.getMaterial((String)dataMap.get("material"));
        int qty = (Integer)dataMap.get("qty");
        String name = (String)dataMap.get("name");
        Integer damage = (Integer)dataMap.get("damage");
        
        GuildCostItem item = new GuildCostItem(material, qty, name, damage);
        return item;
    }
    
    private Material material;
    private int qty;
    private String name;
    private Integer damage;
    
    public GuildCostItem(Material material, int qty, String name, Integer damage) {
        this.material = material;
        this.qty = qty;
        this.name = name;
        this.damage = damage;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public int getQty() {
        return this.qty;
    }
    
    public String getName() {
        return this.name;
    }

    public Integer getDamage() { return this.damage; }

    public ItemStack produceStack() {
        if(damage == null) {
            return new ItemStack(material, qty);
        } else {
            return new ItemStack(material, qty, damage.shortValue());
        }

    }
}
