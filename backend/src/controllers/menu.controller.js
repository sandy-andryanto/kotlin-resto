/**
 * This file is part of the Sandy Andryanto Resto Application.
 *
 * @author     Sandy Andryanto <sandy.andryanto.official@gmail.com>
 * @copyright  2025
 *
 * For the full copyright and license information,
 * please view the LICENSE.md file that was distributed
 * with this source code.
 */

const Menu = require('../models/menu.model')

async function list(req, res) {
    let menuTop = await Menu.find({}).limit(1).sort({ rating: -1 });
    let menu = await Menu.find({}).sort({ rating: -1 });
    menu = menu.map(p => ({
        image: p.image,
        name: p.name,
        category: p.category,
        rating: menuTop.length > 0 ? ((p.rating / parseFloat(menuTop[0].rating)) * 10) : p.rating,
        price: parseFloat(p.price.toString()),
        description: p.description,
    }))
    res.status(200).send(menu);
    return;
}

module.exports = {
    list
}