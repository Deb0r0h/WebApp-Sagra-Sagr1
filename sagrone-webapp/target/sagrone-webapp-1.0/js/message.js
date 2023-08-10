/*
 Copyright 2018-2023 University of Padua, Italy

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Author: Gianluca Rossi (gianluca.rossi.4@studenti.unipd.it)
 Version: 1.0
 Since: 1.0
*/
//this function removes the shown message after three seconds is displayed
window.onload = (event) => {
    var error=document.getElementById("message");
    if(error!=null){
        console.log("message event started!");
        setTimeout(() => {
                error.parentNode.removeChild(error);
                console.log("message event ended!");
        }, 3000);
    }
  };