package app.avare.plugin.contactsFilterPlugin;

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

import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.CursorWrapper;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import app.avare.lib.configparser.JSONParser;

public class Hook_Cursor {

    public static String className = "android.content.ContentResolver";
    public static String methodName = "query";
    public static String methodSig = "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;";

    public static Cursor hook(Object thiz, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d("CPLUGIN", "Uri: " + uri);

        String[] newProjection;
        if (projection != null) {
            boolean containsContactsId = false;
            for (int i = 0; i < projection.length; i++) {
                if (projection[i].equals(ContactsContract.Data.CONTACT_ID)) {
                    containsContactsId = true;
                }
            }
            if (!containsContactsId) {
                newProjection = new String[projection.length + 1];
                for (int i = 0; i < projection.length; i++) {
                    newProjection[i] = projection[i];
                }
                newProjection[projection.length] = ContactsContract.Data.CONTACT_ID;
                for (int i = 0; i < newProjection.length; i++) {
                    Log.i("new projection", newProjection[i]);
                }
            } else {
                newProjection = projection;
            }

        } else {
            newProjection = null;
        }

        Cursor structuredNameCursor = backup(thiz, ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ContactsContract.Data.CONTACT_ID}, ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " != ?", new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, "null"}, ContactsContract.Data.CONTACT_ID);

        //structuredNameCursor.moveToFirst();
        while (structuredNameCursor.moveToNext()) {
            Log.i("STRUCTURED NAME CURSOR", "ID: " + structuredNameCursor.getString(structuredNameCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)));
            Log.i("STRUCTURED NAME CURSOR", "GIVEN_NAME: " + structuredNameCursor.getString(structuredNameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
        }

        Cursor cursor = backup(thiz, uri, newProjection, selection, selectionArgs, sortOrder);

        String contactIDKey;
        if (cursor.getColumnIndex(ContactsContract.Contacts._ID) != -1) {
            contactIDKey = ContactsContract.Contacts._ID;
        } else {
            contactIDKey = ContactsContract.Data.CONTACT_ID;
        }

        String[] newColumns = new String[cursor.getColumnCount() + 1];
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            newColumns[i] = cursor.getColumnName(i);
        }
        newColumns[cursor.getColumnCount()] = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;
        MatrixCursor joinedCursor = new MatrixCursor(newColumns);

        CursorJoiner joiner = new CursorJoiner(cursor, new String[]{contactIDKey}, structuredNameCursor, new String[]{ContactsContract.Data.CONTACT_ID});
        for (CursorJoiner.Result res : joiner) {
            switch (res) {
                case LEFT:
                    Log.i("CURSOR JOINER", "left");

                    break;
                case RIGHT:
                    Log.i("CURSOR JOINER", "right");

                    break;
                case BOTH:
                    Object[] columns = new Object[cursor.getColumnCount() + 1];
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        columns[i] = cursor.getString(i);
                    }
                    columns[cursor.getColumnCount()] = structuredNameCursor.getString(structuredNameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    joinedCursor.addRow(columns);
                    Log.i("CURSOR JOINER", "both");
                    break;
            }
        }

        for (int i = 0; i < joinedCursor.getColumnCount(); i++) {
            Log.i("JOINED CURSOR", joinedCursor.getColumnName(i));
        }

        JSONParser jp = new JSONParser();

        HookCursor hc = new HookCursor(joinedCursor, jp);
        Log.d("CONTACTS HOOK", "cursor Hooked!!");

        return hc;
    }

    public static Cursor backup(Object thiz, Uri uri, String[] projection, String selection, String[] selectionAgs, String sortOrder) {
        //Log.d("BACKUP", "Unfortunately the backup method was called");

        return null;
    }

    private static class HookCursor extends CursorWrapper {

        JSONParser parser;
        JSONArray vertical;
        JSONArray horizontal;

        private String[] notToFilterArray = new String[]{"_id"};
        private ArrayList<String> notToFilter = new ArrayList<String>();

        private HookCursor(Cursor c, JSONParser config) {

            super(c);
            this.parser = config;
            this.vertical = this.parser.getContactsSettings("vertical");
            this.horizontal = this.parser.getContactsSettings("horizontal");
            Log.d("constructor", "Private class HookCursor Constructed!");

            for (String s : notToFilterArray) {
                notToFilter.add(s);
            }

        }

        @Override
        public String getString(int columnIndex) {
            Log.i("HOOK CURSOR", "Calling Cursor getString()");
            try {
                if (JSONParser.jSONArrayContains(this.vertical, super.getColumnName(columnIndex)) || this.notToFilter.contains(super.getColumnName(columnIndex))) {
                    Log.i("VERTICAL", "Config contains " + super.getColumnName(columnIndex) + ", returning");
                    return super.getString(columnIndex);
                } else {
                    Log.i("VERTICAL", "Config doesn't contain " + super.getColumnName(columnIndex) + ", not returning");
                    if (super.getColumnName(columnIndex).equals(ContactsContract.Contacts.DISPLAY_NAME) || this.notToFilter.contains(super.getColumnName(columnIndex))) {
                        String givenName = super.getString(super.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        Log.i("VERTICAL", "Accessing DISPLAY_NAME, returning GIVEN_NAME: " + givenName);
                        return givenName;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";

        }

        @Override
        public boolean moveToNext() {
            try {
                Log.i("HOOK CURSOR", "Using Hook Cursor moveToNext()");
                boolean next = super.moveToNext();
                if (next) {
                    Log.i("HOOK CURSOR", "Querying ID");
                    for (int i = 0; i < this.getColumnCount(); i++) {
                        //Log.i("HOOK CURSOR table", this.getColumnName(i) + ": " + this.getString(i));
                    }
                    String currentID = "";
                    if (this.getColumnIndex(ContactsContract.Data.CONTACT_ID) > -1) {
                        currentID = this.getString(this.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    }
                    if (currentID == null || currentID.equals("")) {
                        currentID = this.getString(this.getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    Log.i("HOOK CURSOR", "Current ID: " + currentID);

                    if (currentID != null && !JSONParser.jSONArrayContains(this.horizontal, currentID)) {

                        Log.i("HOOK CURSOR", "Filtering ID, moving cursor..");

                        boolean foundNext = this.moveToNext();
                        return foundNext;
                    }
                }
                return next;
            } catch (Exception e) {
                Log.i("HOOK CURSOR", "Exception Occured..");
                e.printStackTrace();
                return this.moveToNext();
            }
        }
    }
}
