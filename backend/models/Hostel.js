const db = require('../config/database');

class Hostel {
    constructor(data) {
        this.id = data.id;
        this.name = data.name;
        this.location = data.location;
        this.area = data.area;
        this.available_rooms = data.available_rooms;
        this.total_rooms = data.total_rooms;
        this.price_per_month = data.price_per_month;
        this.amenities = data.amenities;
        this.contact_number = data.contact_number;
        this.description = data.description;
        this.image_url = data.image_url;
        this.is_active = data.is_active;
    }

    // Get all hostels
    static async getAll(filters = {}) {
        try {
            let query = 'SELECT * FROM hostels WHERE is_active = TRUE';
            const values = [];

            // Apply filters
            if (filters.area) {
                query += ' AND area = ?';
                values.push(filters.area);
            }

            if (filters.min_price) {
                query += ' AND price_per_month >= ?';
                values.push(filters.min_price);
            }

            if (filters.max_price) {
                query += ' AND price_per_month <= ?';
                values.push(filters.max_price);
            }

            if (filters.available_rooms) {
                query += ' AND available_rooms > 0';
            }

            query += ' ORDER BY created_at DESC';

            const [rows] = await db.pool.execute(query, values);
            return rows;
        } catch (error) {
            throw new Error(`Error getting hostels: ${error.message}`);
        }
    }

    // Get hostel by ID
    static async getById(id) {
        try {
            const query = 'SELECT * FROM hostels WHERE id = ?';
            const [rows] = await db.pool.execute(query, [id]);
            return rows[0] || null;
        } catch (error) {
            throw new Error(`Error getting hostel: ${error.message}`);
        }
    }

    // Get hostels by area
    static async getByArea(area) {
        try {
            const query = 'SELECT * FROM hostels WHERE area = ? AND is_active = TRUE ORDER BY created_at DESC';
            const [rows] = await db.pool.execute(query, [area]);
            return rows;
        } catch (error) {
            throw new Error(`Error getting hostels by area: ${error.message}`);
        }
    }

    // Create new hostel
    static async create(hostelData) {
        try {
            const query = `
                INSERT INTO hostels (name, location, area, available_rooms, total_rooms, 
                                   price_per_month, amenities, contact_number, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            `;
            
            const values = [
                hostelData.name,
                hostelData.location,
                hostelData.area,
                hostelData.available_rooms || 0,
                hostelData.total_rooms || 0,
                hostelData.price_per_month,
                hostelData.amenities,
                hostelData.contact_number,
                hostelData.description
            ];

            const [result] = await db.pool.execute(query, values);
            return { id: result.insertId, ...hostelData };
        } catch (error) {
            throw new Error(`Error creating hostel: ${error.message}`);
        }
    }

    // Update hostel
    static async update(id, updateData) {
        try {
            const allowedFields = [
                'name', 'location', 'area', 'available_rooms', 'total_rooms',
                'price_per_month', 'amenities', 'contact_number', 'description'
            ];
            
            const updateFields = [];
            const values = [];

            for (const [key, value] of Object.entries(updateData)) {
                if (allowedFields.includes(key) && value !== undefined) {
                    updateFields.push(`${key} = ?`);
                    values.push(value);
                }
            }

            if (updateFields.length === 0) {
                throw new Error('No valid fields to update');
            }

            values.push(id);
            const query = `UPDATE hostels SET ${updateFields.join(', ')} WHERE id = ?`;
            
            const [result] = await db.pool.execute(query, values);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Error updating hostel: ${error.message}`);
        }
    }

    // Delete hostel (soft delete)
    static async delete(id) {
        try {
            const query = 'UPDATE hostels SET is_active = FALSE WHERE id = ?';
            const [result] = await db.pool.execute(query, [id]);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Error deleting hostel: ${error.message}`);
        }
    }

    // Search hostels
    static async search(searchTerm, filters = {}) {
        try {
            let query = `
                SELECT * FROM hostels 
                WHERE is_active = TRUE 
                AND (name LIKE ? OR location LIKE ? OR area LIKE ? OR description LIKE ?)
            `;
            const values = [`%${searchTerm}%`, `%${searchTerm}%`, `%${searchTerm}%`, `%${searchTerm}%`];

            // Apply additional filters
            if (filters.area) {
                query += ' AND area = ?';
                values.push(filters.area);
            }

            if (filters.min_price) {
                query += ' AND price_per_month >= ?';
                values.push(filters.min_price);
            }

            if (filters.max_price) {
                query += ' AND price_per_month <= ?';
                values.push(filters.max_price);
            }

            query += ' ORDER BY created_at DESC';

            const [rows] = await db.pool.execute(query, values);
            return rows;
        } catch (error) {
            throw new Error(`Error searching hostels: ${error.message}`);
        }
    }

    // Get available areas
    static async getAreas() {
        try {
            const query = 'SELECT DISTINCT area FROM hostels WHERE is_active = TRUE ORDER BY area';
            const [rows] = await db.pool.execute(query);
            return rows.map(row => row.area);
        } catch (error) {
            throw new Error(`Error getting areas: ${error.message}`);
        }
    }
}

module.exports = Hostel;


