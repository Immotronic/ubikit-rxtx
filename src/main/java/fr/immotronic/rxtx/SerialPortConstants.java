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

public final class SerialPortConstants 
{
	public static final int  DATABITS_5             =gnu.io.SerialPort.DATABITS_5;
	public static final int  DATABITS_6             =gnu.io.SerialPort.DATABITS_6;
	public static final int  DATABITS_7             =gnu.io.SerialPort.DATABITS_7;
	public static final int  DATABITS_8             =gnu.io.SerialPort.DATABITS_8;
	public static final int  PARITY_NONE            =gnu.io.SerialPort.PARITY_NONE;
	public static final int  PARITY_ODD             =gnu.io.SerialPort.PARITY_ODD;
	public static final int  PARITY_EVEN            =gnu.io.SerialPort.PARITY_EVEN;
	public static final int  PARITY_MARK            =gnu.io.SerialPort.PARITY_MARK;
	public static final int  PARITY_SPACE           =gnu.io.SerialPort.PARITY_SPACE;
	public static final int  STOPBITS_1             =gnu.io.SerialPort.STOPBITS_1;
	public static final int  STOPBITS_2             =gnu.io.SerialPort.STOPBITS_2;
	public static final int  STOPBITS_1_5           =gnu.io.SerialPort.STOPBITS_1_5;
	public static final int  FLOWCONTROL_NONE       =gnu.io.SerialPort.FLOWCONTROL_NONE;
	public static final int  FLOWCONTROL_RTSCTS_IN  =gnu.io.SerialPort.FLOWCONTROL_RTSCTS_IN;
	public static final int  FLOWCONTROL_RTSCTS_OUT =gnu.io.SerialPort.FLOWCONTROL_RTSCTS_OUT;
	public static final int  FLOWCONTROL_XONXOFF_IN =gnu.io.SerialPort.FLOWCONTROL_XONXOFF_IN;
	public static final int  FLOWCONTROL_XONXOFF_OUT=gnu.io.SerialPort.FLOWCONTROL_XONXOFF_OUT;
}
