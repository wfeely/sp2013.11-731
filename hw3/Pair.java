/** Serena Jeblee
* Machine Learning 10-701
* HW3
*/

public class Pair<K, V> {
 
  private K x;
  private V y;
 
  public Pair(K k,V v) {  
    x = k;
    y = v;   
  }

  public void set(K newx, V newy){
	x = newx;
	y = newy;
  }
 
  public K first() {
    return x;
  }
 
  public V second() {
    return y;
  }
 
  public String toString() { 
    return "(" + x + ", " + y + ")";  
  }
}
