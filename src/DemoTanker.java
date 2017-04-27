package uk.ac.nott.cs.g53dia.multidemo;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.nott.cs.g53dia.multilibrary.*;

/**
 * A simple example tanker that chooses actions.
 * 
 * @author Julian Zappala Copyright (c) 2011 Julian Zappala
 * 
 *         See the file "license.terms" for information on usage and
 *         redistribution of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class DemoTanker extends Tanker {
	
	boolean flag = false;
	static Point currentTask = null;
	int time = 0; // used to allow for a specific scouting pattern
	int prevstationX = 0, prevstationY = 0;
	int dist = 0;
	Point station = null;
	static ArrayList<Cell> stationList = new ArrayList<Cell>();
	static ArrayList<Cell> allStationList = new ArrayList<Cell>();
	static ArrayList<Point> wellList = new ArrayList<Point>();
	static ArrayList<Point> bannedList = new ArrayList<Point>();// stores stations that lie outside tankers range
	static ArrayList<Integer> stationX = new ArrayList<Integer>();
	static ArrayList<Integer> stationY = new ArrayList<Integer>();
	static ArrayList<Integer> wellX = new ArrayList<Integer>();
	static ArrayList<Integer> wellY = new ArrayList<Integer>();
	Iterator it = stationList.iterator();
	static Cell previousPosition = null;
	static Cell currentPosition = null;
	static int currentX = 0;
	static int currentY = 0;
	
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
				return new MoveAction(4);
			}
			// searches 25 cells west
			if (time < 66) {
				// System.out.println(x);
				time++;
				previousPosition = currentPosition;
				return new MoveAction(3);
			}

			// searches 25 cells south east
			if (time < 100 && (getFuelLevel() > 35)) {
				time++;
				previousPosition = currentPosition;
				return new MoveAction(6);
			}
			// searches 25 cells west
			if (time < 133 && (getFuelLevel() >= 35)) {
				time++;
				previousPosition = currentPosition;
				return new MoveAction(3);
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
			wellX.add(currentX);
			wellY.add(currentY);
			return new LoadWaterAction();
		}

		// if at a station with a task deposit water
		if (getCurrentCell(view) instanceof Station) {
			stationX.add(currentX);
			stationY.add(currentY);
			prevstationX = currentX;
			prevstationY = currentY;
			Cell c = getCurrentCell(view);
			Task t = ((Station) c).getTask();
			if (t == null) {
				// int index = stationList.indexOf(c);
				stationList.remove(c);
				// stationX.remove(index);
				// stationY.remove(index);
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
			 * Point p = wellList.get(0); return new MoveTowardsAction(p);
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
			/*
			 * dist++; if (dist % 45 == 0) { // prevents tanker from getting
			 * stuck if a station lies outside the 50 cell range
			 * stationList.remove(0); } Point p = stationList.get(nextStation);
			 * return new MoveTowardsAction(p);
			 */

			/*
			 * if (prevstationX != 0 || prevstationY != 0) { Cell c =
			 * view[prevstationX][prevstationY]; if (((Station) c).getTask() !=
			 * null) { return new MoveTowardsAction(station); } } else {
			 */

			int tempx = 50, tempy = 50;
			int indexX = 0, indexY = 0;
			int chosenTemp = 0;
			for (int counter = 0; counter < stationList.size(); counter++) {
				Cell c = stationList.get(counter);
				if (((Station) c).getTask() != null) {
					if (Math.abs(currentX - stationX.get(counter)) < tempx) {
						tempx = Math.abs(currentX - stationX.get(counter));
						indexX = counter;
						station = c.getPoint();
					}
					if (Math.abs(currentY - stationY.get(counter)) < tempy) {
						tempy = Math.abs(currentY - stationY.get(counter));
						indexY = counter;
						station = c.getPoint();
					}
				}
			}
			if (tempy > tempx) {
				chosenTemp = indexX;
				Cell c = stationList.get(chosenTemp);
				station = c.getPoint();
			} else {
				chosenTemp = indexY;
				Cell c = stationList.get(chosenTemp);
				station = c.getPoint();
			}
			dist++;
			if (dist % 45 == 0) { // prevents tanker from getting stuck if a
									// station lies outside the 50 cell range
				stationList.remove(chosenTemp);
				/*
				 * if(y>49 || x > 49 || x<-49 || y<-49){
				 * bannedList.add(station); }
				 */
			}
			currentTask = station;
			previousPosition = currentPosition;
			// }
			return new MoveTowardsAction(station);

		}
		if(allStationList == null){
			int move = (int) (Math.random() * 8);
			return new MoveAction(move);
		}
		
		// Random Search
		else{
		int move = (int) (Math.random() * allStationList.size());
		Point p = allStationList.get(move).getPoint();
		return new MoveTowardsAction(p);
		}
	}
}
