package sna.physica.graph;

import java.util.Set;

public class CIReturnObject {
private Set<Integer> seedNodeSet;
private double fraction;
private int lastRank;
private int seedNodeCount;
public Set<Integer> getSeedNodeSet() {
	return seedNodeSet;
}
public void setSeedNodeSet(Set<Integer> seedNodeSet) {
	this.seedNodeSet = seedNodeSet;
}
public double getFraction() {
	return fraction;
}
public void setFraction(double fraction) {
	this.fraction = fraction;
}
public int getLastRank() {
	return lastRank;
}
public void setLastRank(int lastRank) {
	this.lastRank = lastRank;
}
public int getSeedNodeCount() {
	return seedNodeCount;
}
public void setSeedNodeCount(int seedNodeCount) {
	this.seedNodeCount = seedNodeCount;
}

}
