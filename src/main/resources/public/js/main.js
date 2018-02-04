$(document).ready(function () {
    console.log("setting airports");
    setAirportOptions();
});

function setAirportOptions(){
    ajaxJsonCall('GET', '/api/airports/', null, function(airports) {
        $("#nav-airports").empty();
        $("#nav-airports").append('<li><a href="/airports.html">Manage airports</a></li>');
          airports.forEach(function(airport) {
                console.log(airport);
                $("#nav-airports").append('<li><a href="/flights.html?airport=' + airport.id + '&type=0">' + airport.city + '</a></li>');
          })}
          , null)
}

function countdown(date, flight_nr){
var countDownDate = new Date(date).getTime();

// Update the count down every 1 second
var x = setInterval(function() {

  // Get todays date and time
  var now = new Date().getTime();

  // Find the distance between now an the count down date
  var distance = countDownDate - now;

  // Time calculations for days, hours, minutes and seconds
  var days = Math.floor(distance / (1000 * 60 * 60 * 24));
  var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
  var seconds = Math.floor((distance % (1000 * 60)) / 1000);

  // Display the result in the element with id="demo"
  document.getElementById("countdown_" + flight_nr).innerHTML = days + "d " + hours + "h "
  + minutes + "m " + seconds + "s ";

  // If the count down is finished, write some text
  if (distance < 0) {
    clearInterval(x);
    document.getElementById("countdown_" + flight_nr).innerHTML = "Taken Off";
  }
}, 1000);
}
