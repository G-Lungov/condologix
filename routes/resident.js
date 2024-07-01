var express = require('express');
var router = express.Router();

/* GET resident page. */
router.get('/', function(req, res, next) {
  res.render('../views/resident', { title: 'Resident' });
});

module.exports = router;
