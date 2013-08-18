/********************************************************
 * Copyright (C) 2008 Course Scheduler Team
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of 
 * 	the GNU General Public License as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * 	without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * 	if not, write to:
 * 		Free Software Foundation, Inc.
 * 		59 Temple Place, Suite 330, 
 * 		Boston, MA 02111-1307 USA
********************************************************/

/*********************************************************
 * Course Scheduler
 * File: Preferences.java
 * 
 * Contains class:
 * 
 * 		Preferences:
 * 
 * 			Purpose: To provide an object for storing application
 * 						preferences
 * 
 * @author Mike Reinhold
********************************************************/
package Scheduler;								//declare as member of scheduler package


/*********************************************************
 * The following imports are necessary for operation
********************************************************/
import io.coursescheduler.util.preferences.PreferencesFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;					//to declare as serializeable
import java.util.Calendar;						//to get date information
import java.util.Date;
import java.util.prefs.BackingStoreException;


import com.google.inject.Inject;


/*********************************************************
 * @purpose Class to store preference information for the 
 * 		scheduler
 * 
 * @see Serializable
********************************************************/
public class Preferences implements Serializable {
	
	private static java.util.prefs.Preferences newPreferences;
	
	/*********************************************************
	 * The following are versioning fields within the preferences
	********************************************************/
	protected static final long versionID = 2013010900032L;	//object version
	protected static final long serialVersionUID = 11L 
			+ Version.preferences.id;				//serial ID
		
	
	/*********************************************************
	 * The following are the fields of the preferences for rating courses
	********************************************************/
	private boolean ratingsEnabled;				//overall rating enable
	private boolean rateMyProfessorEnabled;		//RMP ratings enable
	private Period preferred;					//preferred time slot
	private Period longestBreakPer;				//longest break between classes
	private Period shortestBreakPer;			//shortest break between classes
	private boolean dayOff;						//day off rating enable
	private boolean[] daysOff;					//preferred day off array
	private double longestBreak;				//longest break double
	private double shortestBreak;				//shortest break double
	private int updateMin;						//number of days to wait before updating
		
	private boolean downloadUGrad;				//download undergrad courses
	private boolean overRideURL;				//override url enable
	private String URL;							//override url value
	private boolean downloadGrad;               //download grad courses on campus
	private boolean overrideGrad;				//override grad course url enable
	private String gradURL;						//override grad course url value
	private boolean downloadGradDist;           //download grad courses distance learning
	private boolean overrideGradDist;			//Override grad course dist url enable
	private String gradDistURL;					//override grad course dist url value
	private boolean overRideSID;				//override sid enable
	private String SID;							//override sid value
	
	private CourseColor colors;					//the course color object
	
	private int greyCodeLimit;					//the maximum number of grey codes for a build schedule thread
	
	private transient String currentTerm;		//current term identifier, non-serializable
	
	@Inject
	private PreferencesFactory prefFact;
	
	
	/*********************************************************
	 * (Constructor)
	 * 
	 * @purpose Create and initialize the preferences object
	********************************************************/
	public Preferences(){		
		dayOff = false;							//day off is initially false
		daysOff = new boolean[Day.values().length];//days off are all specified to false
		preferred = new Period("8:00 - 6:00pm");	//preferred period is 8 am to 6 pm
		setLongestBreakPer(new Period(480));	//max break is set to 8 hours
		setShortestBreakPer(new Period(1));		//shortest break is set to 1 minute
		ratingsEnabled = true;					//ratings are enabled
		rateMyProfessorEnabled = true;			//rate my professor is enabled
				
		currentTerm = Term.nextTerm();			//get next term for registration
		URL = new String(Main.defURL);					//set default override url to empty
		overRideURL = false;					//disable the url override
		SID = new String(Main.defSID);					//set default override sid to empty
		overRideSID = false;					//disable the sid override		
		downloadUGrad = true;					//undergrad courses enabled
		
		downloadGrad = true;					//on campus grad courses enabled
		overrideGrad = false;					//override on campus courses disabled
		gradURL = new String(Main.defGradCampURL);//default override value
		
		downloadGradDist = true;				//off campus grad courses enabled
		overrideGradDist = false;				//override off campus grad courses disabled
		gradDistURL = new String(Main.defGradDistURL);//default override value
		
		updateMin = 2;							//update time is set to two days
		
		colors = new CourseColor();				//make new course colors
		
		greyCodeLimit = 250;					//make the default limit 200
	}

	/**
	 * Migrate from the custom Preferences class to the Java Preferences class
	 *
	 */
	public void migrate(){
		newPreferences = prefFact.getUserNode("legacy");
		try {
			if(newPreferences.get("migrateDate", null) == null){
				this.setRatingsEnabled(ratingsEnabled);
				this.setRateMyProfessorEnabled(rateMyProfessorEnabled);
				this.setPreferred(preferred);
				this.setLongestBreakPer(longestBreakPer);
				this.setShortestBreakPer(shortestBreakPer);
				this.setDayOff(dayOff);
				this.setDaysOff(daysOff);
				this.setUpdateMin(updateMin);
				this.setDownloadUGrad(downloadUGrad);
				this.setURL(URL);
				this.setDownloadGrad(downloadGrad);
				this.setOverrideGrad(overrideGrad);
				this.setGradURL(gradURL);
				this.setDownloadGradDist(downloadGradDist);
				this.setOverrideGradDist(overrideGradDist);
				this.setGradDistURL(gradDistURL);
				this.setOverRideURL(overRideURL);
				this.setSID(SID);
				this.setColors(colors);
				this.setGreyCodeLimit(greyCodeLimit);
				this.setCurrentTerm(currentTerm);
				
				newPreferences.put("migrateDate", new Date().toString());
				
				newPreferences.exportNode(new FileOutputStream("config/export-user.xml"));
			}
		} catch (BackingStoreException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		} catch (IOException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}

	/*********************************************************
	 * @purpose returns if ratings are enabled
	 *
	 * @return boolean: if the overall ratings are enabled
	********************************************************/
	public boolean isRatingsEnabled() {
		return newPreferences.getBoolean("ratingsEnabled", true);					//return ratings status
	}


	/*********************************************************
	 * @purpose set if overall ratings are enabled
	 * 
	 * @param boolean ratingsEnabled: if the ratings should be enabled
	********************************************************/
	public void setRatingsEnabled(boolean ratingsEnabled) {
		newPreferences.putBoolean("ratingsEnabled", ratingsEnabled);
	}


	/*********************************************************
	 * @purpose return if RMP ratings are enabled
	 * 
	 * @return boolean: if RMP ratings are enabled
	********************************************************/
	public boolean isRateMyProfessorEnabled() {
		return newPreferences.getBoolean("rateMyProfessorEnabled", true);			//return RMP ratings status
	}


	/*********************************************************
	 * @purpose Sets if RMP ratings are enabled
	 * 
	 * @param boolean rateMyProfessorEnabled: if RMP ratings should be enabled
	********************************************************/
	public void setRateMyProfessorEnabled(boolean rateMyProfessorEnabled) {
		newPreferences.putBoolean("rateMyProfessorEnabled", rateMyProfessorEnabled);//set RMP ratings status
	}


	/*********************************************************
	 * @purpose return the preferred class period
	 * 
	 * @return Period: the preferred period in which to have class
	********************************************************/
	public Period getPreferred() {
		return new Period(newPreferences.get("preferred", null));
	}


	/*********************************************************
	 * @purpose set the preferred class period
	 * 
	 * @param Period preferred: the preferred hours to have class
	********************************************************/
	public void setPreferred(Period preferred) {
		newPreferences.put("preferred", preferred.toString());				//set the period
	}


	/*********************************************************
	 * @purpose return the preferred longest break
	 * 
	 * @return Period: the preferred longest break as a period
	********************************************************/
	public Period getLongestBreakPer() {
		return new Period(newPreferences.get("longestBreakPer", null));					//return break as a period
	}


	/*********************************************************
	 * @purpose sets the longest break period between classes
	 * 
	 * @param Period longestBreakPer: the longest break between classes
	********************************************************/
	public void setLongestBreakPer(Period longestBreakPer) {
		newPreferences.put("longestBreakPer", longestBreakPer.toString());//set the period
		newPreferences.putDouble("longestBreak",longestBreakPer.getDurationMin());//set the duration
	}


	/*********************************************************
	 * @purpose return the shortest break period
	 * 
	 * @return Period: the shortest preferred break betweeen classes
	********************************************************/
	public Period getShortestBreakPer() {
		return new Period(newPreferences.get("shortestBreakPer", null));				//return the period
	}


	/*********************************************************
	 * @purpose sets the shortest break period
	 * 
	 * @param Period shortestBreakPer: the shortest preferred break between classes
	********************************************************/
	public void setShortestBreakPer(Period shortestBreakPer) {
		newPreferences.put("shortestBreakPer", shortestBreakPer.toString());//set the period
		newPreferences.putDouble("shortestBreak",shortestBreakPer.getDurationMin());//set the duration
	}

	
	/*********************************************************
	 * @purpose return if a day off is being rated
	 * 
	 * @return boolean: if the day off rating should be used
	********************************************************/
	public boolean hasDayOff() {
		return newPreferences.getBoolean("dayOff", false);						//return the day off status
	}

	
	/*********************************************************
	 * @purpose sets if the day off rating should be used
	 * 
	 * @param boolean dayOff: if the day off rating should be used
	********************************************************/
	public void setDayOff(boolean dayOff) {
		newPreferences.putBoolean("dayOff", dayOff);				//set the day off ratings status
	}	


	/*********************************************************
	 * @purpose return the days preferred off
	 * 
	 * @return boolean[]: if a given day is a preferred days off
	********************************************************/
	public boolean[] getDaysOff() {
		return daysOff;						//TODO return the days off array
	}


	/*********************************************************
	 * @purpose sets the preferred days off 
	 * 
	 * @param boolean[] daysOff: an array specifying which days are 
	 * 		preferred days off
	********************************************************/
	public void setDaysOff(boolean[] daysOff) {
		this.daysOff = daysOff;				//TODO set days off
	}


	/*********************************************************
	 * @purpose return the longest break
	 * 
	 * @return double: the longest preferred break between classes
	********************************************************/
	public double getLongestBreak() {
		return newPreferences.getDouble("longestBreak", 480);				//return the break
	}


	/*********************************************************
	 * @purpose sets the longest preferred break
	 * 
	 * @param double longestBreak: the longest preferred break between classes in minutes
	********************************************************/
	public void setLongestBreak(double longestBreak) {
		setLongestBreakPer(new Period((int)longestBreak));//set the period with that duration
	}


	/*********************************************************
	 * @purpose returns the shortest preferred break between classes
	 * 
	 * @return double: the minimum number of minutes preferred between classes
	********************************************************/
	public double getShortestBreak() {
		return newPreferences.getDouble("shortestBreak", 1);				//return break
	}


	/*********************************************************
	 * @purpose sets the shortest preferred break between classes
	 * 
	 * @param double shortestBreak: the preferred shortest break in minutes
	********************************************************/
	public void setShortestBreak(double shortestBreak) {
		setShortestBreakPer(new Period((int)shortestBreak));//set to new period with duration
	}


	/*********************************************************
	 * @purpose Returns the current term
	 * 
	 * @return String: the current term in query string form aka "summer08"
	********************************************************/
	public String getCurrentTerm() {
		return newPreferences.get("currentTerm", Term.nextTerm());					//return current term
	}


	/*********************************************************
	 * @purpose sets the current term
	 * 
	 * @param String currentTerm: the term to set the term to
	********************************************************/
	public void setCurrentTerm(String currentTerm) {
		newPreferences.put("currentTerm", (currentTerm==null) ? Term.nextTerm() : currentTerm);		//set the current term
	}


	/*********************************************************
	 * @purpose Return the min number of days before updating database
	 * 
	 * @return int: the min number of days between auto updates
	********************************************************/
	public int getUpdateMin() {
		return newPreferences.getInt("updateMin", 2);					//return the days
	}


	/*********************************************************
	 * @purpose Sets the minimum number of days between auto updates
	 * 
	 * @param int updateMin: the minimum number of days between updates
	********************************************************/
	public void setUpdateMin(int updateMin) {
		newPreferences.putInt("updateMin", updateMin);			//set the days
	}
	
	
	/*********************************************************
	 * @purpose validates a given year to determine if it is plausible 
	 * 		for querying
	 * 
	 * @param String yearStr: the year to check for validity
	 * 
	 * @return boolean: if the param is a valid year
	********************************************************/
	public static boolean validTermYear(String yearStr){
		Calendar calendar = Calendar.getInstance();//get current calandar day
		int enteredYear;					//make int for enetered year
		int currentYear = calendar.get(Calendar.YEAR);//get current year
		
		try{								//try to parse the year from the string
			enteredYear = Integer.parseInt(yearStr);//and save to the entered year
		}
		catch(Exception ex){				//if parsing fails
			return false;					//return invalid
		}
		
		int diff = Math.abs(currentYear - enteredYear);//else get the difference between the years
		
		if (diff > 5){						//and if year difference is greater than five
			return false;					//return invalid
		}
		return true;						//else return valid year
	}

	
	/*********************************************************
	 * @purpose Returns the url to use for queries, either the
	 * 		default url or the override url depending if overriding
	 * 		is enabled
	 * 
	 * @return String: the url to use for querying
	********************************************************/
	public String getURL() {
		return (isOverRideURL()) ? getURLInternal() : Main.defURL;//return override url if enabled else default url
	}

	/*********************************************************
	 * @purpose Returns the internal url to use for queries, either the
	 * 		default url or the override url depending if overriding
	 * 		is enabled
	 * 
	 * @return String: the internal url to use for querying
	********************************************************/
	private String getURLInternal(){
		return newPreferences.get("URL", Main.defURL);
	}
	
	/*********************************************************
	 * @purpose Sets the override url
	 * 
	 * @param String url: the string to set the override url to
	********************************************************/
	public void setURL(String url) {
		newPreferences.put("URL", url);						//set the url
	}

	
	/*********************************************************
	 * @prupose return the SID to use for querying
	 * 
	 * @return String: the sid to use for querying depending on if 
	 * 		sid overriding is enabled
	********************************************************/
	public String getSID() {
		return (isOverRideSID()) ? getSIDInternal() : Main.defSID;//return overrid sid if enabled, else default
	}
	
	/*********************************************************
	 * @prupose return the stored SID to use for querying
	 * 
	 * @return String: the stored sid to use for querying depending on if 
	 * 		sid overriding is enabled
	********************************************************/
	private String getSIDInternal(){
		return newPreferences.get("SID", Main.defSID);
	}

	
	/*********************************************************
	 * @purpose Sets the overrid sid
	 * 
	 * @param String sid: the override sid to use
	********************************************************/
	public void setSID(String sid) {
		newPreferences.put("SID", sid);							//set the sid
	}

	
	/*********************************************************
	 * @purpose Returns if the override url is enabled
	 * 
	 * @return boolean: if override url is enabled
	********************************************************/
	public boolean isOverRideURL() {
		return newPreferences.getBoolean("overRideURL", false);						//return the override url status
	}

	
	/*********************************************************
	 * @purpose Sets the override url enable
	 * 
	 * @param boolean overRideURL: if the url override should be enabled
	********************************************************/
	public void setOverRideURL(boolean overRideURL) {
		newPreferences.putBoolean("overRideURL", overRideURL);			//set the override url status
	}


	/*********************************************************
	 * @purpose returns if the override sid is enabled
	 * 
	 * @return boolean: if override sid is enabled
	********************************************************/
	public boolean isOverRideSID() {
		return newPreferences.getBoolean("overRideSID", false);						//return override sid status
	}


	/*********************************************************
	 * @purpose Sets the overrride sid enable
	 * 
	 * @param boolean overRideSID: if the overrid sid should be enabled
	********************************************************/
	public void setOverRideSID(boolean overRideSID) {
		newPreferences.putBoolean("overRideSID", overRideSID);			//set overrid sid enable
	}
	
	
	/********************************************************
	 * @purpose Saves the preference to the default pref file
	 * 
	 * @return boolean: if the save was successful
	*********************************************************/
	@Deprecated
	public boolean save(){
		return Serial.save((Main.dataFolder + "Prefs" + Main.preferencesExt), this);
	}
	
	
	/********************************************************
	 * @purpose Loads the preference from the default pref file
	 * 
	 * @return Preferences: the prefs loaded from the file
	*********************************************************/
	@Deprecated
	public static Preferences load(){
		return Serial.load(Main.dataFolder + "Prefs" + Main.preferencesExt);
	}


	/********************************************************
	 * @purpose Return the color storage structure
	 * 
	 * @return CourseColor: the structure holding the course
	*********************************************************/
	public CourseColor getColors() {
		CourseColor colors;
		try {
			ByteArrayInputStream bos = new ByteArrayInputStream(newPreferences.getByteArray("colors", null));
			ObjectInputStream out = new ObjectInputStream(bos);   
			colors = (CourseColor)out.readObject();
		} catch (IOException | ClassNotFoundException e) {
			colors = new CourseColor();
			setColors(colors);
		}
		return colors;
	}


	/*********************************************************
	 * @purpose Sets the course colors used to render the schedules
	 * 
	 * @param CourseColor colors: the course colors
	********************************************************/
	public void setColors(CourseColor colors) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);   
			out.writeObject(colors);
			newPreferences.putByteArray("colors", bos.toByteArray());
		} catch (IOException e) {
			// TODO CATCH STUB
			e.printStackTrace();
		}
	}


	/*********************************************************
	 * @purpose Return the maximum number of grey codes per thread
	 * 
	 * @return int: the number of grey codes per thread
	********************************************************/
	public int getGreyCodeLimit() {
		return newPreferences.getInt("greyCodeLimit", 200);					//return the grey code limit
	}


	/*********************************************************
	 * @purpose Set the grey code limit
	 * 
	 * @param int greyCodeLimit: the maximum number of grey codes per thread
	********************************************************/
	public void setGreyCodeLimit(int greyCodeLimit) {
		newPreferences.putInt("greyCodeLimit", greyCodeLimit);		//set the grey code limit
	}


	/*********************************************************
	 * @purpose Return if on campus graduate courses should be downloaded
	 * 
	 * @return boolean: if on campus graduate courses should be downloaded
	********************************************************/
	public boolean isDownloadGrad() {
		return newPreferences.getBoolean("downloadGrad", true);					//return if campus grad courses should be downloaded
	}


	/*********************************************************
	 * @purpose Set if on campus grad courses are downloaded
	 * 
	 * @param boolean downloadGrad: if on campus grad courses should be downloaded
	********************************************************/
	public void setDownloadGrad(boolean downloadGrad) {
		newPreferences.putBoolean("downloadGrad", downloadGrad);		//set if courses should be downloaded
	}


	/*********************************************************
	 * @purpose Return if the override URL should be used
	 * 
	 * @return boolean: if on campus override should be used
	********************************************************/
	public boolean isOverrideGrad() {
		return newPreferences.getBoolean("overrideGrad", false);					//return if the campus grad url should be used
	}


	/*********************************************************
	 * @purpose Set if the override grad URL is to be used
	 * 
	 * @param boolean overrideGrad: if the override url is to be used
	********************************************************/
	public void setOverrideGrad(boolean overrideGrad) {
		newPreferences.putBoolean("overrideGrad", overrideGrad);		//set if override url should be used
	}


	/*********************************************************
	 * @purpose Return the override URL to be used
	 * 
	 * @return String: the override URL
	********************************************************/
	public String getGradURL() {
		return newPreferences.get("gradURL", Main.defGradCampURL);							//return the override URL
	}


	/*********************************************************
	 * @purpose Set the Override URL for downloading on campus grad courses
	 * 
	 * @param String gradURL: the URL to use
	********************************************************/
	public void setGradURL(String gradURL) {
		newPreferences.put("gradURL", gradURL);					//set the override URL
	}


	/*********************************************************
	 * @purpose Return if distance grad courses should be downloaded
	 * 
	 * @return boolean: if off campus graduate courses should be downloaded
	********************************************************/
	public boolean isDownloadGradDist() {
		return newPreferences.getBoolean("downloadGradDist", true);				//return if off campus grad courses are downloaded
	}


	/*********************************************************
	 * @purpose Set if distance learning grad courses should be downloaded
	 * 
	 * @param boolean downloadGradDist: if courses should be downloaded
	********************************************************/
	public void setDownloadGradDist(boolean downloadGradDist) {
		newPreferences.putBoolean("downloadGradDist", downloadGradDist);//set if should download
	}


	/*********************************************************
	 * @purpose Return if the override url should be used
	 * 
	 * @return boolean: if the override url is to be used
	********************************************************/
	public boolean isOverrideGradDist() {
		return newPreferences.getBoolean("overrideGradDist", false);				//return if override url should be used
	}


	/*********************************************************
	 * @purpose Set if the override URL should be used for downloading distance learning grad courses
	 * 
	 * @param boolean overrideGradDist: if the override URL should be used
	********************************************************/
	public void setOverrideGradDist(boolean overrideGradDist) {
		newPreferences.putBoolean("overrideGradDist", overrideGradDist);//set if override
	}


	/*********************************************************
	 * @purpose Return the override URL for off campus grad courses
	 * 
	 * @return String: the URL for off campus grad courses
	********************************************************/
	public String getGradDistURL() {
		return newPreferences.get(gradDistURL, Main.defGradDistURL);						//return the override url
	}


	/*********************************************************
	 * @purpose Set the override URL for distance learning grad courses download
	 * 
	 * @param String gradDistURL: the URL to use for downloading
	********************************************************/
	public void setGradDistURL(String gradDistURL) {
		newPreferences.put("gradDistURL", gradDistURL);			//set the URL to download from
	}


	/*********************************************************
	 * @purpose Return if undergraduate courses should be downloaded
	 * 
	 * @return boolean: if undergrad courses have been downloaded
	********************************************************/
	public boolean isDownloadUGrad() {
		return newPreferences.getBoolean("downloadUGrad", true);					//return if undergrad should be downloaded
	}


	/*********************************************************
	 * @purpose Set if undergrad courses should be downloaded
	 * 
	 * @param boolean downloadUGrad: if undergrad courses should be downloaded
	********************************************************/
	public void setDownloadUGrad(boolean downloadUGrad) {
		newPreferences.putBoolean("downloadUGrad", downloadUGrad);		//set if should download
	}
}
