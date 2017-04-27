package uk.ac.nott.cs.g53dia.multidemo;

import java.util.ArrayList;
import java.util.Iterator;
import uk.ac.nott.cs.g53dia.multilibrary.*;

public class ReactiveTanker extends DemoTanker {

	int time = 0; // used to allow for a specific scouting pattern
	int prevstationX = 0, prevstationY = 0;
	int x = 0, y = 0;
	int dist = 0;
	Point station = null;
	Iterator it = stationList.iterator();
	Cell previousPosition = null;
	Cell currentPosition = null;

	public Action senseAndAct(Cell[][] view, long timestep) {

		currentPosition = getCurrentCell(view);

		Calculator.senseAndAct(view, timestep);

		// while loop which contains my hard-coding of the scouting pattern
		// I wish for my Tanker to initially take
		while (time < 133) {
			if ((getFuelLevel() < 35) && !(getCurrentCell(view) instanceof FuelPump)) {
				previousPosition = currentPosition;
				// time++;
				return new MoveTowardsAction(FUEL_PUMP_LOCATION);
			}
			if ((getFuelLevel() < (MAX_FUEL / 2) + 5) && (getCurrentCell(view) instanceof FuelPump)) {
				time++;
				return new RefuelAction();
			}

			// searches 25 cells north-east
			if (time < 33) {
				time++;
				previousPosition = currentPosition;
				return new MoveAction(5);
			}
			// searches 25 cells west
			if (time < 66) {
				// System.out.println(x);
				time++;
				previousPosition = currentPosition;
				return new MoveAction(2);
			}

			// searches 25 cells south east
			if (time < 100 && (getFuelLevel() > 35)) {
				time++;
				previousPosition = currentPosition;
				return new MoveAction(7);
			}
			// searches 25 cells west
			if (time < 133 && (getFuelLevel() >= 35)) {
				time++;
				previousPosition = currentPosition;
				return new MoveAction(2);
			}

		}

		// If fuel tank is low and not at the fuel pump then move towards the
		// fuel pump
		// (getFuelLevel() <= x) || (getFuelLevel() <= y )
		if ((getFuelLevel() < (MAX_FUEL / 2) + 5) && !(getCurrentCell(view) instanceof FuelPump)) {
			previousPosition = currentPosition;
			return new MoveTowardsAction(FUEL_PUMP_LOCATION);
		}

		// If at a well load water
		if (getCurrentCell(view) instanceof Well && (getWaterLevel() < MAX_WATER)) {
			wellX.add(x);
			wellY.add(y);
			return new LoadWaterAction();
		}

		// if at a station with a task deposit water
		if (getCurrentCell(view) instanceof Station) {
			stationX.add(x);
			stationY.add(y);
			prevstationX = x;
			prevstationY = y;
			Cell c = getCurrentCell(view);
			Task t = ((Station) c).getTask();
			if (t == null) {
				stationList.remove(c);
			}
			if (t != null && getWaterLevel() > 1) {
				// System.out.println(getScore());
				return new DeliverWaterAction(t);
			}
		}

		// if at fueling station refill fuel
		if ((getFuelLevel() < (MAX_FUEL / 2) + 5) && (getCurrentCell(view) instanceof FuelPump)) {
			return new RefuelAction();
		}

		// returns to the nearest well if water level is 0
		if (getWaterLevel() == 0 && wellList != null) {
			/*
			 * Point p = wellList.get(0); previousPosition = currentPosition;
			 * return new MoveTowardsAction(p);
			 */

			Point p = null;
			int tempx = 50, tempy = 50;
			int chosenIndexX = 0, chosenIndexY = 0;
			for (int counter = 0; counter < wellList.size(); counter++) {
				if (Math.abs(currentX - wellX.get(counter)) < tempx) {
					tempx = Math.abs(currentX - wellX.get(counter));
					chosenIndexX = wellX.get(counter);
				}
				if (Math.abs(currentY - wellY.get(counter)) < tempx) {
					tempy = Math.abs(currentY - wellY.get(counter));
					chosenIndexY = wellY.get(counter);
				}
			}
			if (tempy > tempx) {
				p = wellList.get(chosenIndexX);
			} else {
				p = wellList.get(chosenIndexY);
			}
			previousPosition = currentPosition;

			return new MoveTowardsAction(p);

		}

		if (it.hasNext() && stationList != null && getWaterLevel() > 0 && !(getCurrentCell(view) instanceof Station)) {
			Cell c = stationList.get(0);
			station = c.getPoint();
			dist++;
			if (dist % 45 == 0) { // prevents tanker from getting stuck if a
									// station lies outside the 50 cell range
				stationList.remove(0);
			}
			int i = 0;
			for (Cell elem : stationList) {
				c = stationList.get(i);
				station = c.getPoint();
				if (DemoTanker.currentTask != station) {
					break;
				}
				i++;
			}

			currentTask = station;
			previousPosition = currentPosition;
			return new MoveTowardsAction(station);

		}
		// Random Search
		else {
			int move = (int) (Math.random() * allStationList.size());
			Point p = allStationList.get(move).getPoint();
			return new MoveTowardsAction(p);
		}
	}
}