// VARIABLES



let productsTable = document.getElementById("products_table");
let buttonsDiv = document.getElementById("buttons");
let form = document.getElementById("form");
let basketSessionId = "";
let userSessionId = "";
let userSessionName = "";











// ONLOAD OPERATIONS



cleanHTML();

loadProductsTable();

getUserSessionId()
  .then(function(result) {
    if (result === "") {
      console.log("No user session ID");
    } else{
      userSessionId = result;
      console.log(result + " - user session ID");
    }
  })
  .catch(function(err) {
    console.log(err);
  });

getUserSessionUsername()
  .then(function(result) {
    if (result === "") {
      console.log("No user session username");
    } else{
    userSessionName = result;
    console.log(result + " - user session username");
    }
  })
  .catch(function(err) {
    console.log(err);
  });

getLastUsersBasket()
  .then(function(result) {
    if (result === "") {
      getBasketSession()
      .then(function(result) {
        if (result === "") {
          console.log("No basket session ID");
        } else {
          basketSessionId = result;
          console.log(result + " basket session ID");
        }
      })
      .catch(function(err) {
        console.log(err);
      });
    } else {
      basketSessionId = result;
      console.log(result + " basket session ID");
    }
  })
  .catch(function(err) {
    console.log(err);
  });











function getUserSessionId() {
  return new Promise(function(resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
    };
    xhr.onerror = reject;
    xhr.open("GET", "rest/usersession/getUserId");
    xhr.send();
  });
}



function getUserSessionUsername() {
  return new Promise(function(resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
    };
    xhr.onerror = reject;
    xhr.open("GET", "rest/usersession/getUsername");
    xhr.send();
  });
}



function getBasketSession() {
  return new Promise(function(resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
    };
    xhr.onerror = reject;
    xhr.open("GET", "rest/basketSession");
    xhr.send();
  });
}











// PAGE lOADING



function loadProductsTable() {
  let getProductListRequest = new XMLHttpRequest();
  getProductListRequest.open("GET", "rest/products");
  getProductListRequest.setRequestHeader("Content-Type", "application/json");
  getProductListRequest.onload = function () {
    let response = getProductListRequest.responseText;
    let ourjsonData = JSON.parse(response);
    cleanHTML();
    renderProductsTableHTML(ourjsonData);
  };
  getProductListRequest.send();
}



function loadBasketsItemList(id) {
  let xhr = new XMLHttpRequest();
  xhr.open("GET", "rest/basketitems/" + id);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function () {
    let response = xhr.responseText;
    let convertToJson = JSON.parse(response);
    cleanHTML();
    renderBasketsListHTML(convertToJson);
  };
  xhr.send();
}



function loadAddNewShipmentForm() {
  let xhr = new XMLHttpRequest();
  xhr.open("GET", "rest/products");
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function () {
    let response = xhr.responseText;
    let convertToJson = JSON.parse(response);
    cleanHTML();
    renderOrderProductsFormHTML(convertToJson);
  };
  xhr.send();
}











// PAGE RENDERING



function renderProductsTableHTML(jsonData){
  let productsTableThead = document.createElement("thead");
  let productsTableTbody = document.createElement("tbody");
  let productsTableRow = document.createElement("tr");
  let productsTableCell = document.createElement("td");
  let productsTableButton = document.createElement("button")

  productsTableCell.textContent = "Name";
  productsTableRow.appendChild(productsTableCell);

  productsTableCell = document.createElement("td");
  productsTableCell.textContent = "Quantity";
  productsTableRow.appendChild(productsTableCell);

  productsTableCell = document.createElement("td");
  productsTableCell.innerHTML = "Price";
  productsTableRow.appendChild(productsTableCell);

  productsTableCell = document.createElement("td");
  productsTableCell.innerHTML = "Actions";
  productsTableCell.colspan = "1";
  productsTableRow.appendChild(productsTableCell);

  productsTableThead.appendChild(productsTableRow);
  productsTable.appendChild(productsTableThead);
  jsonData.forEach(function (product) {
    productsTableRow = document.createElement("tr");

    productsTableCell = document.createElement("td");
    productsTableCell.textContent = product.name;
    productsTableCell.id = "name" + product.id;
    productsTableRow.appendChild(productsTableCell);

    productsTableCell = document.createElement("td");
    productsTableCell.textContent = product.quantity;
    productsTableRow.appendChild(productsTableCell);

    productsTableCell = document.createElement("td");
    productsTableCell.innerHTML = product.price;
    productsTableCell.id = "price" + product.id;
    productsTableRow.appendChild(productsTableCell);

    productsTableCell = document.createElement("td");
    let productsTableInput = document.createElement("input");
    productsTableInput.id = "quantity" + product.id;
    productsTableInput.placeholder = "enter quantity";
    productsTableCell.appendChild(productsTableInput);
    productsTableRow.appendChild(productsTableCell);

    productsTableCell = document.createElement("td");
    productsTableButton = document.createElement("button");
    productsTableButton.innerHTML = "Add To Basket";
    productsTableButton.setAttribute("onClick", "addItemToBasket(" + product.id + ")");
    productsTableCell.appendChild(productsTableButton);
    productsTableRow.appendChild(productsTableCell);

    productsTableTbody.appendChild(productsTableRow);
    productsTable.appendChild(productsTableTbody);

  });
  let loadBasketsItemList = document.createElement("button");
  loadBasketsItemList.setAttribute("onClick", "checkTheBasket()");
  loadBasketsItemList.innerHTML = "Check the Basket"
  buttonsDiv.appendChild(loadBasketsItemList);

  let loginButton = document.createElement("button");
  loginButton.setAttribute("onClick", "loadLoginFormHTML()");
  loginButton.innerHTML = "Login"

  let logoutButton = document.createElement("button");
  logoutButton.setAttribute("onClick", "logout()");
  logoutButton.innerHTML = "Logout";
  getUserSessionId()
    .then(function(result) {
      if (result === "") {
        buttonsDiv.appendChild(loginButton);
      } else {
        buttonsDiv.appendChild(logoutButton);
      }
    })
    .catch(function(err) {
      console.log(err);
    });
  let orderNewProductsButton = document.createElement("button");
  orderNewProductsButton.setAttribute("onClick", "loadAddNewShipmentForm()");
  orderNewProductsButton.innerHTML = "Add New Shipment";
  buttonsDiv.appendChild(orderNewProductsButton);
}



function renderBasketsListHTML(jsonData) {
  let basketTableListThead = document.createElement("thead");
  let basketTableListTbody = document.createElement("tbody");
  let basketTableListRow = document.createElement("tr");
  let basketTableListCell = document.createElement("td");
  let basketTableListButton = document.createElement("button")

  basketTableListCell.textContent = "Name";
  basketTableListRow.appendChild(basketTableListCell);

  basketTableListCell = document.createElement("td");
  basketTableListCell.textContent = "Quantity";
  basketTableListRow.appendChild(basketTableListCell);

  basketTableListCell = document.createElement("td");
  basketTableListCell.innerHTML = "Price";
  basketTableListRow.appendChild(basketTableListCell);

  basketTableListCell = document.createElement("td");
  basketTableListCell.innerHTML = "Actions";
  basketTableListCell.colspan = "3";
  basketTableListRow.appendChild(basketTableListCell);

  basketTableListThead.appendChild(basketTableListRow);
  productsTable.appendChild(basketTableListThead);
  jsonData.forEach(function (basket_item) {
    basketTableListRow = document.createElement("tr");

    basketTableListCell = document.createElement("td");
    basketTableListCell.textContent = basket_item.product.name;
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListCell = document.createElement("td");
    basketTableListCell.textContent = basket_item.quantity;
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListCell = document.createElement("td");
    basketTableListCell.innerHTML = basket_item.price;
    basketTableListCell.id = "price" + basket_item.id;
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListCell = document.createElement("td");
    let basketTableListInput = document.createElement("input");
    basketTableListInput.id = "quantity" + basket_item.id;
    basketTableListInput.placeholder = "enter new quantity";
    basketTableListCell.appendChild(basketTableListInput);
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListCell = document.createElement("td");
    basketTableListButton = document.createElement("button");
    basketTableListButton.innerHTML = "Edit";
    basketTableListButton.setAttribute("onClick", "EditItemInBasket(" + basket_item.id + "," + basket_item.basket.id + "," + basket_item.product.id + ")");
    basketTableListCell.appendChild(basketTableListButton);
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListCell = document.createElement("td");
    basketTableListButton = document.createElement("button");
    basketTableListButton.innerHTML = "Delete";
    basketTableListButton.setAttribute("onClick", "removeItemFromBasket(" + basket_item.id + "," + basket_item.basket.id + ")");
    basketTableListCell.appendChild(basketTableListButton);
    basketTableListRow.appendChild(basketTableListCell);

    basketTableListTbody.appendChild(basketTableListRow);
    productsTable.appendChild(basketTableListTbody);
    basketSessionId = basket_item.basket.id;
  });
  basketTableListButton = document.createElement("button");
  basketTableListButton.setAttribute("onClick", "checkOutBasket(" + basketSessionId +")");
  basketTableListButton.innerHTML = "Check Out";
  buttonsDiv.appendChild(basketTableListButton);

  basketTableListButton = document.createElement("button");
  basketTableListButton.setAttribute("onClick", "loadProductsTable()");
  basketTableListButton.innerHTML = "Go Back";
  buttonsDiv.appendChild(basketTableListButton);
}



function loadLoginFormHTML() {
  cleanHTML();
  let input = document.createElement("input");
  let loginForm = document.createElement("form");
  input.id = "username";
  input.type = "text";
  input.value = "admin";
  input.placeholder = "Username";
  loginForm.appendChild(input);

  input = document.createElement("input");
  input.id = "password";
  input.type = "password";
  input.value = "admin";
  input.placeholder = "Password";
  loginForm.appendChild(input);

  let loginButton = document.createElement("button");
  loginButton.innerHTML = "Login";
  loginButton.setAttribute("onClick", "login(" + basketSessionId + ")");
  loginButton.type = "button";
  loginForm.appendChild(loginButton);

  let registerButton = document.createElement("button");
  registerButton.innerHTML = "Register";
  registerButton.setAttribute("onClick", "register()");
  registerButton.type = "button";
  loginForm.appendChild(registerButton);

  let goBackButton = document.createElement("button");
  goBackButton.innerHTML = "Go Back";
  goBackButton.setAttribute("onClick", "loadProductsTable()");
  goBackButton.type = "button";
  loginForm.appendChild(goBackButton);
  form.appendChild(loginForm);
}

function renderOrderProductsFormHTML(jsonData) {
  let orderProductsFormThead = document.createElement("thead");
  let orderProductsFormTbody = document.createElement("tbody");
  let orderProductsFormRow = document.createElement("tr");
  let orderProductsFormCell = document.createElement("td");
  let orderProductsFormButton = document.createElement("button")

  orderProductsFormCell.textContent = "Name";
  orderProductsFormRow.appendChild(orderProductsFormCell);

  orderProductsFormCell = document.createElement("td");
  orderProductsFormCell.textContent = "Quantity";
  orderProductsFormRow.appendChild(orderProductsFormCell);

  orderProductsFormCell = document.createElement("td");
  orderProductsFormCell.innerHTML = "Actions";
  orderProductsFormCell.colspan = "3";
  orderProductsFormRow.appendChild(orderProductsFormCell);

  orderProductsFormThead.appendChild(orderProductsFormRow);
  productsTable.appendChild(orderProductsFormThead);

  let productId = 0;
  jsonData.forEach(function (product) {
    orderProductsFormRow = document.createElement("tr");

    orderProductsFormCell = document.createElement("td");
    orderProductsFormCell.textContent = product.name;
    orderProductsFormRow.appendChild(orderProductsFormCell);

    orderProductsFormCell = document.createElement("td");
    orderProductsFormCell.textContent = product.quantity;
    orderProductsFormRow.appendChild(orderProductsFormCell);

    orderProductsFormCell = document.createElement("td");
    let orderProductsFormInput = document.createElement("input");
    orderProductsFormInput.id = "quantity" + product.id;
    orderProductsFormInput.placeholder = "Enter order number";
    orderProductsFormCell.appendChild(orderProductsFormInput);
    orderProductsFormRow.appendChild(orderProductsFormCell);

    orderProductsFormCell = document.createElement("td");
    orderProductsFormInput = document.createElement("input");
    orderProductsFormInput.id = "productId" + productId++;
    orderProductsFormInput.type = "hidden";
    orderProductsFormInput.value = product.id;
    orderProductsFormCell.appendChild(orderProductsFormInput);
    orderProductsFormRow.appendChild(orderProductsFormCell);

    orderProductsFormTbody.appendChild(orderProductsFormRow);
    productsTable.appendChild(orderProductsFormTbody);
  });

  goBackButton = document.createElement("button");
  goBackButton.setAttribute("onClick", "loadProductsTable()");
  goBackButton.innerHTML = "Go Back";
  buttonsDiv.appendChild(goBackButton);

  orderProductsFormButton = document.createElement("button");
  orderProductsFormButton.innerHTML = "Order";
  orderProductsFormButton.setAttribute("onClick", "orderNewProducts()");
  buttonsDiv.appendChild(orderProductsFormButton);
}











// USER SESSION OPERATIONS



function login(basketId) {
  return new Promise(function(resolve, reject) {
    let user = {
      username: document.getElementById("username").value,
      password: document.getElementById("password").value
    }
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
      let response = xhr.responseText;
      if(response === ""){
        alert("Incorrect username or password");
      } else {
        location.reload();
      }
    };
    xhr.onerror = reject;
    xhr.open("POST", "rest/usersession");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(user));
  });
}

function loginAfterRegister(username,password) {
  return new Promise(function(resolve, reject) {
    let user = {
      username: username,
      password: password
    }
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
      let response = xhr.responseText;
      if(response === ""){
        alert("Incorrect username or password");
      } else {
        location.reload();
      }
    };
    xhr.onerror = reject;
    xhr.open("POST", "rest/usersession");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(user));
  });
}



function register(){
  return new Promise(function(resolve, reject) {
    let user = {
      username: document.getElementById("username").value,
      password: document.getElementById("password").value
    }
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
      let response = xhr.responseText;
      if(response === ""){
        alert("User with that username already exists");
      } else {
        loginAfterRegister(user.username,user.password)
          .then(function(result) {
          })
          .catch(function(err) {
            console.log(err);
          });
      }
    };
    xhr.onerror = reject;
    xhr.open("POST", "rest/users/");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(user));
  });
}



function logout() {
  var ourRequest = new XMLHttpRequest();
  ourRequest.open("DELETE", "rest/usersession/logout");
  ourRequest.setRequestHeader("Content-Type", "application/json");
  ourRequest.onload = function () {
    location.reload();
  };
  ourRequest.send();
}











// BASKET OPERATIONS



function checkOutBasket(basketId) {
  if (userSessionId === "") {
    cleanHTML();
    loadLoginFormHTML();
    alert("Sign in or Register First")
    return;
  }
    let basket = {
      id: basketId,
      paymentDate: new Date()
    }
    var ourRequest = new XMLHttpRequest();
    ourRequest.open("PUT", "rest/basket/");
    ourRequest.setRequestHeader("Content-Type", "application/json");
    ourRequest.onload = function () {
      let response = ourRequest.responseText;
      destroyBasket();
      cleanHTML();
      loadProductsTable();
    };
    ourRequest.send(JSON.stringify(basket));
}


function checkTheBasket() {
// CHECK FOR BASKET SESSION
  getLastUsersBasket()
    .then(function(lastUsersBasketId) {
      // IF THERE IS NO PREVIOUS BASKETS,GET CURRENT BASKET
      if (lastUsersBasketId === "") {
        getBasketSession()
          .then(function(basketSession) {
            if (basketSession === "") {
              alert("No items in the basket")
            } else {
              console.log(basketSession + " basket session ID");
              loadBasketsItemList(basketSession);
            }
          })
          .catch(function(err) {
            console.log(err);
          });
      } else {
        loadBasketsItemList(lastUsersBasketId);
      }
    })
    .catch(function(err) {
      console.log(err);
    });
}



function getLastUsersBasket() {
  return new Promise(function(resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
    };
    xhr.onerror = reject;
    xhr.open("GET", "rest/basketSession/unfinishedBasketSession");
    xhr.send();
  });
}











// Session



function createNewBasket() {
  return new Promise(function(resolve, reject) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(this.responseText);
    };
    xhr.onerror = reject;
    xhr.open("POST", "rest/basketSession");
    xhr.send();
  });
}



function destroyBasket() {
  let xhr = new XMLHttpRequest();
  xhr.open("DELETE", "rest/basketSession");
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function () {
    location.reload();
  };
  xhr.send();
}



function connectUserToBasket(basketId) {
  getLastUsersBasket()
    .then(function(result) {
      // IF THERE IS NO PREVIOUS BASKETS,GET CURRENT BASKET
      if (result === "") {
        getBasketSession()
        .then(function(basketSession) {
          if (basketSession === "") {
            console.log("No basket session ID");
          } else {
            console.log(basketSession + " basket session ID");
            return basketSession;
          }
        })
        .catch(function(err) {
          console.log(err);
        });
      } else {
        console.log(result + " basket session ID");
        return  result;
      }
    })
    .catch(function(err) {
          console.log(err);
    });
}



function addItemToBasket(productsId) {
  let quantity = document.getElementById("quantity" + productsId).value;
  if (quantity === "") {
    return;
  }
  getLastUsersBasket()
    .then(function(lastUsersBasketId) {
      // IF THERE IS NO PREVIOUS BASKETS,GET CURRENT BASKET
      if (lastUsersBasketId === "") {
        getBasketSession()
         .then(function(basketSessionId) {
           if (basketSessionId === "") {
             // CHECK FOR BASKET SESSION ID, IF THERE"S NO BASKET, CREATE NEW ONE, AND REPEAT FUNCTION AGAIN
             createNewBasket()
               .then(function(newBasketId) {
                 addItemToBasket(productsId)
               })
               .catch(function(err) {
                 console.log(err);
               });
            } else {
             var basketItem = {
               price: document.getElementById("price" + productsId).textContent,
               quantity: document.getElementById("quantity" + productsId).value
             }
             var xhr = new XMLHttpRequest();
             getUserSessionId()
               .then(function(userId) {
                 if (userId === "") {
                   userId = 0;
                 }
                 xhr.open("POST", "rest/basketitems/" + basketSessionId + "/" + productsId + "/" + userId);
                 xhr.setRequestHeader("Content-Type", "application/json");
                 xhr.onload = function () {
                   console.log("with basketSessionId " + basketSessionId );
                   cleanHTML();
                   loadProductsTable();
                 };
                 xhr.send(JSON.stringify(basketItem));
               })
               .catch(function(err) {
                 console.log(err);
               });
           }
         })
         .catch(function(err) {
           console.log(err);
         });
      } else {
        var basketItem = {
          price: document.getElementById("price" + productsId).textContent,
          quantity: document.getElementById("quantity" + productsId).value
        }
        var xhr = new XMLHttpRequest();
        getUserSessionId()
          .then(function(userId) {
            if (userId === "") {
              userId = null;
            }
            xhr.open("POST", "rest/basketitems/" + lastUsersBasketId + "/" + productsId + "/" + userId);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.onload = function () {
              console.log("with lastUsersBasketId " + lastUsersBasketId);
              cleanHTML();
              loadProductsTable();
            };
            xhr.send(JSON.stringify(basketItem));
          })
          .catch(function(err) {
            console.log(err);
          });
      }
    })
    .catch(function(err) {
      console.log(err);
    });
}



function removeItemFromBasket(basketItemsId, basketId) {
  var xhr = new XMLHttpRequest();
  xhr.open("DELETE", "rest/basketitems/" + basketItemsId + "/" + basketId);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function () {
    console.log(basketSessionId);
    cleanHTML();
    loadBasketsItemList(basketId);
  };
  xhr.send();
}



function EditItemInBasket(basketItemsId, basketId, productsId) {
  let basketItem = {
    price: document.getElementById("price" + basketItemsId).textContent,
    quantity: document.getElementById("quantity" + basketItemsId).value,
    id: basketItemsId
  }
  let xhr = new XMLHttpRequest();
  xhr.open("PUT", "rest/basketitems/" + basketId);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onload = function () {
    let response = xhr.responseText;
      cleanHTML();
      loadBasketsItemList(basketId);
    };
  xhr.send(JSON.stringify(basketItem));
}



function getProduct(id) {
  return new Promise(function(resolve, reject) {
    let xhr = new XMLHttpRequest();
    xhr.onload = function() {
      resolve(JSON.parse(this.responseText));
      let response = this.responseText;
      let ourjsonData = JSON.parse(response);
    };
    xhr.onerror = reject;
    xhr.open("GET", "rest/products/" + id);
    xhr.send();
  });
}











// Shipments scripts



function orderNewProducts() {
  return new Promise(function(resolve, reject) {
    let shipmentItem = [];
    let i = 0;
    let items = [];
    while (true) {
      if (document.getElementById("productId" + i)== null) {
        break;
      }
      let productId = document.getElementById("productId" + i++).value;
      items.push(getProduct(productId));
    }
    Promise.all(items)
      .then(function(values) {
        for (var i = 0; i < values.length; i++) {
          let quantity = document.getElementById("quantity"+values[i].id).value;
          let item = {
            quantity: (quantity === "") ? 0 : quantity,
            product: {
              id: values[i].id,
              name: values[i].name,
              price: values[i].price,
              quantity: values[i].quantity
            }
          };
          shipmentItem.push(item);
        }

        let xhr = new XMLHttpRequest();
        xhr.onload = function() {
          if (xhr.responseText === "") {
            alert("Error accured! Try to sign in as admin to add new shipment items");
          }
          resolve(this.responseText);
          cleanHTML();
          loadAddNewShipmentForm();
        };
        xhr.onerror = reject;
        xhr.open("POST", "rest/shipmentitems/");
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify(shipmentItem));
      })
      .catch(function(err) {
        console.log(err);
      });
  })
}











// UTILS



function cleanHTML() {
 productsTable.innerHTML = "";
 buttonsDiv.innerHTML = "";
 form.innerHTML = "";
}
