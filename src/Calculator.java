package uk.ac.nott.cs.g53dia.multidemo;

import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Point;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

public class Calculator {
	
	public static void senseAndAct(Cell[][] view, long timestep) {

		// Loop to scan every cell in the tankers view for points of interest
		int i = 0, j = 0;
		for (i = 0; i < 25; i++) {
			for (j = 0; j < 25; j++) {
				Cell c = view[i][j];
				
				//Point p = view[i][j].getPoint();
				if (c == DemoTanker.previousPosition) { //checks where tanker is relevant to where it was
					if (i == 13 && j == 13) {
						break;
					}
					if (i == 12 && j == 14) {
						//x+= Calculator.calcX(i,j);
						DemoTanker.currentX -= 1;
						DemoTanker.currentY += 1;
					}
					if (i == 13 && j == 14) {
						DemoTanker.currentY += 1;
					}
					if (i == 14 && j == 14) {
						DemoTanker.currentX += 1;
						DemoTanker.currentY += 1;
					}
					if (i == 12 && j == 13) {
						DemoTanker.currentX -= 1;
					}
					if (i == 14 && j == 13) {
						DemoTanker.currentX += 1;
					}
					if (i == 12 && j == 12) {
						DemoTanker.currentX -= 1;
						DemoTanker.currentY -= 1;
					}
					if (i == 13 && j == 12) {
						DemoTanker.currentY -= 1;
					}
					if (i == 14 && j == 12) {
						DemoTanker.currentX += 1;
						DemoTanker.currentY -= 1;
					}
				}
				if (view[i][j] instanceof Station) {
					if (((Station) c).getTask() != null) {
						if (i < 13 && !DemoTanker.stationX.contains(DemoTanker.currentX - i)) {
							DemoTanker.stationX.add(DemoTanker.currentX - i);
						}
						if (i > 13 && !DemoTanker.stationX.contains(DemoTanker.currentX - i)) {
							DemoTanker.stationX.add(DemoTanker.currentX + i);
						}
						if (j < 13 && !DemoTanker.stationY.contains(DemoTanker.currentY - j)) {
							DemoTanker.stationY.add(DemoTanker.currentY - j);
						}
						if (j > 13 && !DemoTanker.stationY.contains(DemoTanker.currentY - j)) {
							DemoTanker.stationY.add(DemoTanker.currentY + j);
						}

						if (DemoTanker.stationList.contains(view[i][j]) || DemoTanker.bannedList.contains(view[i][j])) {
							break;
						}
						else {
							DemoTanker.stationList.add(view[i][j]);
							DemoTanker.allStationList.add(view[i][j]);
						}
					}
				}

				if (view[i][j] instanceof Well) {
					if (i < 13 && !DemoTanker.wellX.contains(DemoTanker.currentX - i)) {
						DemoTanker.wellX.add(DemoTanker.currentX - i);
					}
					if (i > 13 && !DemoTanker.wellX.contains(DemoTanker.currentX + i)) {
						DemoTanker.wellX.add(DemoTanker.currentX + i);
					}
					if (j < 13 && !DemoTanker.wellY.contains(DemoTanker.currentY - j)) {
						DemoTanker.wellY.add(DemoTanker.currentY - j);
					}
					if (j > 13 && !DemoTanker.wellY.contains(DemoTanker.currentY - j)) {
						DemoTanker.wellY.add(DemoTanker.currentY + j);
					}
					Point p = view[i][j].getPoint();
					if (DemoTanker.wellList.contains(p)) {
						break;
					} else {
						DemoTanker.wellList.add(p);
					}
				}
			}
		}
	}
}
