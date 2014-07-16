/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//      Contributors:      Xu Lijia 

package ci.xlj.libs.procinvoker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import ci.xlj.libs.utils.StringUtils;

public class ProcInvoker {

	private Logger logger = Logger.getLogger(ProcInvoker.class);

	public ProcInvoker(String command) {
		this.command = command;
		logger.debug("command: " + command);
	}

	private String command;

	public String getCommand() {
		return command;
	}

	private StringBuilder outputBuilder;

	private Process process;

	private String defaultCharset = "utf-8";

	private String output;
	private String errorMessage;

	public int invoke(String charset) {
		int returnCode = Integer.MIN_VALUE;

		try {
			process = Runtime.getRuntime().exec(command);

			// retrieve output
			InputStreamReader inputStreamReader = new InputStreamReader(
					process.getInputStream(),
					StringUtils.isValid(charset) ? charset : defaultCharset);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			outputBuilder = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				outputBuilder.append(line + "\n");
			}

			bufferedReader.close();
			inputStreamReader.close();

			output = outputBuilder.toString();

			inputStreamReader = new InputStreamReader(process.getErrorStream(),
					StringUtils.isValid(charset) ? charset : defaultCharset);
			bufferedReader = new BufferedReader(inputStreamReader);

			outputBuilder = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				outputBuilder.append(line + "\n");
			}

			bufferedReader.close();
			inputStreamReader.close();

			errorMessage = outputBuilder.toString();

			returnCode = process.waitFor();
			process.destroy();

		} catch (Exception e) {
			logger.error("Error in invoking command. Details:\n"
					+ StringUtils.getStrackTrace(e));
		}

		return returnCode;
	}

	public int invoke() {
		return invoke(defaultCharset);
	}

	public String getOutput() {
		return output;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
