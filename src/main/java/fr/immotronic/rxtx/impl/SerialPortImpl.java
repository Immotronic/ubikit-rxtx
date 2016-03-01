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
 package fr.immotronic.rxtx.impl;


import fr.immotronic.rxtx.SerialPort;
import fr.immotronic.rxtx.SerialPortListener;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TooManyListenersException;
import java.util.Vector;

public class SerialPortImpl implements SerialPort
{	
	byte[] buffer = new byte[10240];
	private SerialPortManager manager;
	private String portName;
	private int baudrate;
	private int databits;
	private int stopbits;
	private int parity;
	private InputStream	in;
	private OutputStream out;
	private Vector<SerialPortListener> listeners;
	private gnu.io.SerialPort sp;
	private CommPortIdentifier portIdentifier;
	private Thread monitoringThread;
	private Monitoring monitor;
	
	//private boolean TEMPFIX_SERIAL_CALLBACK_WERE_CALLED; // NO MORE NEEDED
	
	private class Monitoring implements Runnable, SerialPortEventListener
	{
		public void run()
		{
			while(true)
			{
				try 
				{ 
					int len = in.available();
					if(Logger.debug) {
						if(len > 0) {
							Logger.debugTrace("SerialPortImpl: Monitor: " + len + " bytes available for reading. Theorically "+listeners.size()+" are listening.");
						}
					}
					/*if(len > 0 && !TEMPFIX_SERIAL_CALLBACK_WERE_CALLED) 
					{
						//if(debug) if(len > 0) System.out.println("SerialPortImpl: Monitor: artificial call of serialEvent()");
						//serialEvent(new SerialPortEvent(sp, SerialPortEvent.DATA_AVAILABLE, false, false));
					}*/
					//Thread.sleep(50);
					Thread.sleep(1000);
				}
				catch(IOException e)
				{
					// This exception raised if the serial device was disconnected.
					if(Logger.debug) {
						Logger.debugTrace("SerialPortImpl: " + portName + " monitoring thread was stopped.");
					}
					
					notifySerialPortDisappearing(); // Notify all listeners
					return;
				}
				catch(InterruptedException e)
				{
					// This exception raised if the monitoring thread is stopped while sleeping. 
					// This is the expected behavior and run() method MUST be exited in that case.
					if(Logger.debug) {
						Logger.debugTrace("SerialPortImpl: Monitor: Thread was interrupted while sleeping.");
					}
					return; 
				}
				catch(Exception e) 
				{
					Logger.logErr("SerialPortImpl: " + portName + " monitoring thread has raised an exception and stopped", e);
					return; 
				}
			}
		}
		
		@Override
		public void serialEvent(SerialPortEvent eventType) 
		{
			if(Logger.debug) {
				Logger.debugTrace("SerialPortImpl.serialEvent(): before synchronized.");
			}
			try
			{
				synchronized(this)
				{
					if(Logger.debug) {
						Logger.debugTrace("SerialPortImpl.serialEvent(): IN synchronized.");
					}
					
					if(eventType.getEventType() == SerialPortEvent.DATA_AVAILABLE)
					{
						int len = 0;
						
				        try
				        {
				        	len = in.available();
				        	int read_len = in.read(buffer, 0, len);
				        	
				        	if(Logger.debug) {
								StringBuilder sb = new StringBuilder();
								sb.append("SerialPortImpl: New data on " + portName + ": " + len + " bytes were read ( " + read_len + " bytes were available to read): ");
								for(int i = 0; i < len; i++)
								{
									sb.append(Integer.toHexString(buffer[i] & 0xff)).append(" ");
								}
								Logger.debugTrace(sb.toString());
							}
				        }
				        catch(IOException e)
				        {
				        	Logger.logErr(portName + ": <" + eventType.getEventType()+">  ... but an I/O exception occured while reading", e);
				        	notifySerialPortDisappearing();
				        	return;
				        }
				        catch (Exception e)
				        {
				        	Logger.logErr(portName + ": <" + eventType.getEventType()+">  ... but an exception occured while reading", e);
				        	notifySerialPortDisappearing();
				        	return;
				        }
				        
				        try
			        	{
					        Iterator<SerialPortListener> it = listeners.iterator();
					        while(it.hasNext())
					        {
					        		it.next().serialPortReceptionEvent(buffer, len);
					        }
			        	}
			        	catch(Exception e)
			        	{
			        		Logger.logErr(portName + ": <" + eventType.getEventType()+">  ... but an exception occured while distributed received data.", e);
			        	}
					}
					else {
						if(Logger.debug) {
							Logger.debugTrace("SerialPortImpl: " + portName + ": a serial port event occured and it was NOT new data. EventType=" + eventType.getEventType());
						}
					}
				}
			}
			finally
			{
				if(Logger.debug) {
					Logger.debugTrace("SerialPortImpl.serialEvent(): after synchronized.");
				}
			}
		}
	}
	
	public SerialPortImpl(String portName, int baudrate, int databits, int stopbits, int parity, SerialPortManager manager) 
			throws IOException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException
	{
		//TEMPFIX_SERIAL_CALLBACK_WERE_CALLED = false; // NO MORE NEEDED
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl: opening serial port: " + portName);
		}
		
		this.manager = manager;
		this.portName = portName;
		this.baudrate = baudrate;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
		monitor = new Monitoring();
		
		openPort();
		
		listeners = new Vector<SerialPortListener>();
		monitoringThread = new Thread(monitor);
		monitoringThread.start();
	}
	
	public void setFlowControlMode( int flowcontrol ) throws UnsupportedCommOperationException
	{
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl.setFlowControlMode(): before synchronized.");
		}
		
		synchronized(this)
		{
			sp.setFlowControlMode(flowcontrol);
			try
			{
				in = sp.getInputStream();
				out = sp.getOutputStream();
			}
			catch(IOException e)
			{
				notifySerialPortDisappearing();
			}
			
			if(Logger.debug) {
				Logger.debugTrace("SerialPortImpl: " + portName + " serial port flow control mode was setted.");
			}
		}
	}
	
	public void addSerialPortListener(SerialPortListener listener)
	{
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl.addSerialPortListener(): before synchronized.");
		}
		
		synchronized(this)
		{
			
			if(Logger.debug) {
				Logger.debugTrace("SerialPortImpl.addSerialPortListener(): IN synchronized.");
			}
			
			listeners.add(listener);
		}
		
		
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl.addSerialPortListener(): after synchronized.");
		}
	}
	
	public void removeSerialPortListener(SerialPortListener listener)
	{
		if(listener != null) {
			_removeSerialPortListener(listener);
		}
	}
	
	private void _removeSerialPortListener(SerialPortListener listener)
	{
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl._removeSerialPortListener(): before synchronized.");
		}
		
		synchronized(this)
		{
			if(Logger.debug) {
				Logger.debugTrace("SerialPortImpl._removeSerialPortListener(): IN synchronized.");
			}
			
			if(listener != null) listeners.remove(listener);
			else listeners.removeAllElements();
			
			if(listeners.isEmpty()) 
			{
				if(Logger.debug)
				{
					String trace = "SerialPortImpl: ";
					if(listener != null) trace += " no more listener, ";
					trace += "closing " + portName + " serial port...";
					Logger.debugTrace(trace);
				}
				if(monitoringThread != null) 
				{
					monitoringThread.interrupt();
					monitoringThread = null;
				}
				if(sp != null) 
				{
					sp.removeEventListener(); // semble indispensable pour le branchement/débranchement à chaud d'un dispositif série. Tendance à crasher la JVM si la lib binaire ne match pas bien la lib java.
					sp.close();
					sp = null;
					in = null;
					out = null;
				}
				
				if(Logger.debug) {
					Logger.debugTrace("SerialPortImpl: " + portName + " was closed.");
				}
				
				manager.serialPortShouldBeRemoved(portName);
			}
		}
		
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl._removeSerialPortListener(): after synchronized.");
		}
	}
	
	public void write(byte[] data) throws IOException
	{
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl.write(): before synchronized.");
		}
		
		try
		{
			synchronized(this)
			{
				if(Logger.debug) {
					Logger.debugTrace("SerialPortImpl.write(): IN synchronized.");
					
					StringBuilder sb = new StringBuilder();
					sb.append("SerialPortImpl: Writing data on the serial port: ");
					int len = data.length;
					for(int i = 0; i < len; i++)
					{
						sb.append(Integer.toHexString(data[i] & 0xff)).append(" ");
					}
					Logger.debugTrace(sb.toString());
				}
				
				try
				{
					if(out != null) out.write(data);
				}
				catch(IOException e)
		        {
		        	notifySerialPortDisappearing();
		        	throw new IOException();
		        }
			}
		}
		finally
		{
			if(Logger.debug) {
				Logger.debugTrace("SerialPortImpl.write(): after synchronized.");
			}
		}
	}
	
	private void openPort() throws IOException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException
	{
		portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		sp = (gnu.io.SerialPort) portIdentifier.open(this.getClass().getName(),2000);
		
		try 
		{
			sp.addEventListener(monitor);
		} 
		catch (TooManyListenersException e) 
		{
			// Because gnu.io.SerialPort is encapsulated and addEventListener() 
			// is kept private, TooManyListenersException will be never raised.
			// => Nothing is done in this catch block. An error trace is generated.
			Logger.logErr("SerialPortImpl: addEventListener() after opening port: TooManyListenersException. THIS SHOULD NEVER HAPPEN.", e);
		}
		sp.notifyOnDataAvailable(true);
		sp.notifyOnBreakInterrupt(true);
		sp.notifyOnFramingError(true);
		sp.notifyOnOverrunError(true);
		sp.notifyOnParityError(true);
		sp.notifyOnRingIndicator(true);
		
		sp.setSerialPortParams(baudrate, databits, stopbits, parity);
		try
		{
			in = sp.getInputStream();
			out = sp.getOutputStream();
		}
		catch(IOException e)
		{
			notifySerialPortDisappearing();
		}
		
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl: " + portName + " serial port was configured. (in=" + in + ")");
		}
	}
	
	private void notifySerialPortDisappearing()
	{
		if(Logger.debug) {
			Logger.debugTrace("SerialPortImpl: " + portName + " serial port is disappearing.");
		}
		
		Iterator<SerialPortListener> it = listeners.iterator();
        while(it.hasNext())
        {
        		it.next().serialPortHasDisappeared(portName);
        }
        
        _removeSerialPortListener(null); // Remove all listeners to close the serial port.
	}
}
