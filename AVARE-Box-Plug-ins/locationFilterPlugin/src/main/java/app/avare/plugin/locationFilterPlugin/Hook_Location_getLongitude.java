package app.avare.plugin.locationFilterPlugin;

/*
 * Created by AVARE Project 2018/08/23
 */

/*
        Copyright 2016-2018 AVARE project team

        AVARE-Project was financed by the Baden-Württemberg Stiftung gGmbH (www.bwstiftung.de).
        Project partners are FZI Forschungszentrum Informatik am Karlsruher
        Institut für Technologie (www.fzi.de) and Karlsruher
        Institut für Technologie (www.kit.edu).

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

import android.util.Log;

/*
even though fields and methods seem unused they can not be deleted, they are used by yahfa to hook calls to functions
 */

public class Hook_Location_getLongitude {
    public static String className = "android.location.Location";
    public static String methodName = "getLongitude";
    public static String methodSig = "()D";


    public static double hook(Object thiz) {
        Log.i("YAHFA", "Location getLongitude hooked");

        double x = Hook_Location_getLatitude.hook(thiz); //call getLatitude once to make sure the values are initialized
        return Hook_Location_getLatitude.getCurrentFakePos().longitude;

    }
    public static double backup(Object thiz) {
        Log.i("YAHFA", "Location getLongitude hooked");
        return 0.0;
    }



}
