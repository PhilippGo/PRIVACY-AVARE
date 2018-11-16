/*
        Copyright 2016-2018 AVARE project team

        AVARE-Project was financed by the Baden-Württemberg Stiftung gGmbH (www.bwstiftung.de).
        Project partners are FZI Forschungszentrum Informatik am Karlsruher
        Institut für Technologie (www.fzi.de) and Karlsruher
        Institut für Technologie (www.kit.edu).

        Files under this folder (and the subfolders) with "Created by AVARE Project ..."-Notice
	    are our work and licensed under Apache Licence, Version 2.0"

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
import React from 'react';
import { View } from 'react-native';
import { List, withTheme } from 'react-native-paper';
import PreferencesHeader from '../../_shared/PreferencesHeader';

class ContactsFilterPreferences extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      contextID: this.props.navigation.state.params.contextID,
      context: this.props.navigation.state.params.context
    }
  }

  render() {
    let { colors } = this.props.theme;
    return (
      <View style={{ backgroundColor: colors.background }}>
        <PreferencesHeader title="Kontaktfilter" />
        <List.Item 
          title="Personen freigeben"
          onPress={() => {
            this.props.navigation.navigate('ContactItemsFilter', { contextID: this.state.contextID, context: this.state.context });
        }} />
        <List.Item 
          title="Felder freigeben"
          onPress={() => {
            this.props.navigation.navigate('ContactFieldsFilter', { contextID: this.state.contextID, context: this.state.context });
        }} />
      </View>
    );

  }
}

export default withTheme(ContactsFilterPreferences);