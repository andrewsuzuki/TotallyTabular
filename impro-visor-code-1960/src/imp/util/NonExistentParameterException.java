/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.util;

/**
 *
 * @author keller
 */

@SuppressWarnings("serial")
public class NonExistentParameterException extends Exception
{
/**
 * Constructs an instance of
 * <code>NonExistentParameterException</code> with the specified detail message.
 *
 * @param msg the detail message.
 */
public NonExistentParameterException(String msg)
  {
    super(msg);
  }


public String getParameter()
  {
    return super.getMessage();
  }
}
