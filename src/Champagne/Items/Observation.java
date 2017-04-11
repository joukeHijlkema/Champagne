/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Champagne.Items;

/**
 *
 * @author hylkema
 */
public class Observation extends Object {
  
  public int ID,PERIODE,ELEVE;
  public String OBSERVATION;

  public Observation(int ID, int PERIODE, int ELEVE, String TXT) {
    this.ID = ID;
    this.PERIODE = PERIODE;
    this.ELEVE = ELEVE;
    this.OBSERVATION = TXT;
  }
  
  @Override
  public String toString() {
    return String.format("%s", this.OBSERVATION);
  }
  
}
