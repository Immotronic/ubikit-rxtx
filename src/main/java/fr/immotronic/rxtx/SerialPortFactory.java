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
 package fr.immotronic.rxtx;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import fr.immotronic.rxtx.impl.Logger;
import fr.immotronic.rxtx.impl.SerialPortImpl;
import fr.immotronic.rxtx.impl.SerialPortManager;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class SerialPortFactory
{	
	private static class DisappearanceListener implements SerialPortManager
	{
		public void serialPortShouldBeRemoved(String portName)
		{
			ports.remove(portName);
			if(Logger.debug) {
				Logger.debugTrace("SerialPortFactoryImpl: port removed from HashMap.");
			}
		}
	}
	
	private static final DisappearanceListener disappearanceListener = new DisappearanceListener();
	private static final HashMap<String, SerialPortImpl> ports = new HashMap<String, SerialPortImpl>();
	
	
	public static SerialPort open(String portName, int baudrate, int databits, int stopbits, int parity) throws IOException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException
	{
		SerialPortImpl serialPort =  new SerialPortImpl(portName, baudrate, databits, stopbits, parity, disappearanceListener);
		ports.put(portName, serialPort);
		
		if(Logger.debug) {
			Logger.debugTrace("SerialPortFactoryImpl: port added to HashMap.");
		}
		
		return (SerialPort) serialPort;
	}
	
	public static Vector<String> list(boolean only_serialusb_port)
	{
		if(Logger.debug) {
    		Logger.debugTrace("Build serial port List");
    		Logger.debugTrace("----------------------");
    	}
		
		Vector<String> res = new Vector<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();

            if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
            	String portName = portIdentifier.getName();
            	if(Logger.debug) {
            		Logger.debugTrace("Detecting "+portName+" serial port");
            	}
            	if(	!only_serialusb_port || 
            		(only_serialusb_port && portName.contains("tty") && (portName.contains("usb") || portName.contains("USB"))))
            	{
            		res.add(portName);
            	}
            }
        }
        
        return res;
	}
}
