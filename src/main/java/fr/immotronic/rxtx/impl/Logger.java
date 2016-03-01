/*
 * Copyright (c) Immotronic, 2012
 *
 * Contributors:
 *
 *  	Lionel Balme (lbalme@immotronic.fr)
 *  	Kevin Planchet (kplanchet@immotronic.fr)
 *
 * This file is part of ubikit-rxtx, a component of the UBIKIT project.
 *
 * This software is a computer program whose purpose is to host third-
 * parties applications that make use of sensor and actuator networks.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * As a counterpart to the access to the source code and  rights to copy,
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *
 * CeCILL-C licence is fully compliant with the GNU Lesser GPL v2 and v3.
 *
 */

// -----
// TODO: 	Update the package name.
//
// 	Note: 
//		fr.immotronic.rxtx should be something like COUNTRY_CODE.Immotronic.ubikit.apps.
//			For instance: fr.immotronic.ubikit.apps
//
// 		rxtx4osgi should be replaced with the value you specified in
// 		build.xml  & packaging_info.bnd files.
// -----
package fr.immotronic.rxtx.impl; // PACKAGE_PREFIX & APPLICATION_NAME must be replaced.

// ----- 
//			The rest of the file does NOT need to change
// -----

import java.text.SimpleDateFormat;

public final class Logger {
	
	
	public static final boolean debug = Boolean.parseBoolean(System.getProperty("fr.immotronic.rxtx.debug"));
	
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS] ");
	
	public static void debugTrace(String message) {
		if(debug) {
			System.err.println(dateFormat.format(System.currentTimeMillis())+" <DEBUG> "+message);
		}
	}
	
	public static void log(String message) {
		System.out.println(dateFormat.format(System.currentTimeMillis())+message);
	}
	
	public static void logWarn(String message) {
		System.err.println(dateFormat.format(System.currentTimeMillis())+" <WARNING> "+message);
	}
	
	public static void logWarn(String message, Exception e) {
		System.err.println(dateFormat.format(System.currentTimeMillis())+" <WARNING> "+message+" (Exception stack below)");
		e.printStackTrace(System.err);
	}
	
	public static void logErr(String message) {
		System.err.println(dateFormat.format(System.currentTimeMillis())+" <ERROR> "+message);
	}
	
	public static void logErr(String message, Exception e) {
		System.err.println(dateFormat.format(System.currentTimeMillis())+" <ERROR> "+message+" (Exception stack below)");
		e.printStackTrace(System.err);
	}
}
