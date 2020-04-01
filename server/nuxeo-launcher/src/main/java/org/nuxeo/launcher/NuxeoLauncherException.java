/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      Kevin Leturc <kleturc@nuxeo.com>
 */

package org.nuxeo.launcher;

/**
 * @since 11.1
 */
public class NuxeoLauncherException extends RuntimeException {

    private final int errorValue;

    protected NuxeoLauncherException(String message, int errorValue) {
        super(message);
        this.errorValue = errorValue;
    }

    protected NuxeoLauncherException(String message, int errorValue, Throwable cause) {
        super(message, cause);
        this.errorValue = errorValue;
    }

    public int getErrorValue() {
        return errorValue;
    }

}
