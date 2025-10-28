import axios from "axios";

const API_BASE = "http://localhost:8080/api";


export const api = {
    getAuthor:() => axios.get(API_BASE + "/author"),
    addAuthor:(newAuthor) => axios.post(API_BASE + "/author" , newAuthor),
    deleteAuthor:(id) => axios.delete(API_BASE + "/author/" + id),

    getBook:() => axios.get(API_BASE + "/book"),
    addBook:(newBook) => axios.post(API_BASE + "/book" , newBook),
    deleteBook:(id) => axios.delete(API_BASE + "/book/" + id),

    getMember:() => axios.get(API_BASE + "/member"),
    addMember:(newMember) => axios.post(API_BASE + "/member" , newMember),
    deleteMember:(id) => axios.delete(API_BASE + "/member/" + id)

};
