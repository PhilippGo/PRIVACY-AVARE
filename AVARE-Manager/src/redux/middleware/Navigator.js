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
//not used
/*
import React, { Component } from 'react'
import { connect } from 'react-redux'
import { createStackNavigator, StackNavigator, addNavigationHelpers } from 'react-navigation'
import {
    createReactNavigationReduxMiddleware,
    reduxifyNavigator,
    createNavigationReducer,
} from 'react-navigation-redux-helpers'

import  HomeScreen  from '../../views/HomeScreen'
import TransferScreen from '../../views/TransferScreen'
import Items from '../../views/Items'


export const Navigator = createStackNavigator(
    {
        Home: { screen: HomeScreen },
        Items: { screen: Items },
        Transfer: { screen: TransferScreen }
    },
    {
        initialRouteName: 'Home',

    })

export const navigationMiddleware = createReactNavigationReduxMiddleware(
    'root',
    state => state.nav,
);



export const Nav = reduxifyNavigator(Navigator, 'root');

const mapStateToProps = state => ({
    state: state.nav,
})

export const AppWithNavigationState = connect(mapStateToProps)(Nav)
*/