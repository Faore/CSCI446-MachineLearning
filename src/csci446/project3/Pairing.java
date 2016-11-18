/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csci446.project3;

/**
 *
 * @author Lukasrama
 */
public class Pairing implements Comparable<Pairing> {
    public final double distance;
    public final int id;
    
    public Pairing(int id, double distance){
        this.distance = distance;
        this.id = id;
    }

    @Override
    public int compareTo(Pairing other) {
        return Double.valueOf(this.distance).compareTo(other.distance);
    }
}