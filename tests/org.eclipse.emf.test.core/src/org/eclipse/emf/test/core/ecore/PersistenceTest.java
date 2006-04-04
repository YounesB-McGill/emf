/**
 * <copyright>
 *
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: PersistenceTest.java,v 1.8 2006/04/04 14:33:33 marcelop Exp $
 */
package org.eclipse.emf.test.core.ecore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lib.Address;
import lib.Book;
import lib.Cafeteria;
import lib.LibFactory;
import lib.Library;
import lib.Person;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.test.core.TestUtil;

public class PersistenceTest extends TestCase
{  
  private EObject john;
  private EObject mary;
  private EObject herbie;
  
  private EAttribute name;
  private EAttribute brand;
  private EReference children;
  private EReference father;
  private EReference cars;
  
  public PersistenceTest(String name)
  {
    super(name);
  }

  public static Test suite()
  {
    TestSuite ts = new TestSuite("PersistenceTest");
    ts.addTest(new PersistenceTest("testOneZipFile"));
    ts.addTest(new PersistenceTest("testOneTextFile"));
    ts.addTest(new PersistenceTest("testOneTextAndOneZipFiles"));
    ts.addTest(new PersistenceTest("testTwoZipFiles"));
    ts.addTest(new PersistenceTest("testTwoTextFiles"));
    ts.addTest(new PersistenceTest("testEDataType"));
    ts.addTest(new PersistenceTest("testCrossResourceContainment_XMLResourceUUID"));
    ts.addTest(new PersistenceTest("testCrossResourceContainment_Dynamic_ResourceSet"));
    ts.addTest(new PersistenceTest("testCrossResourceContainment_Static_ResourceSet"));
    ts.addTest(new PersistenceTest("testCrossResourceContainment_RemoveChild"));
    return ts;
  }
  
  protected void setUp()
  {
    EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
    pack.setName("pack");
    pack.setNsPrefix("pack");
    pack.setNsURI("http://mypack");
    EPackage.Registry.INSTANCE.put(pack.getNsURI(), pack);
    
    EClass person = EcoreFactory.eINSTANCE.createEClass();
    pack.getEClassifiers().add(person);
    person.setName("Person");
    
    name = EcoreFactory.eINSTANCE.createEAttribute();
    person.getEStructuralFeatures().add(name);
    name.setName("name");
    name.setEType(EcorePackage.eINSTANCE.getEString());
    
    children = EcoreFactory.eINSTANCE.createEReference();
    person.getEStructuralFeatures().add(children);
    children.setName("children");
    children.setEType(person);
    children.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
    
    father = EcoreFactory.eINSTANCE.createEReference();
    person.getEStructuralFeatures().add(father);
    father.setName("father");
    father.setEType(person);

    children.setEOpposite(father);
    father.setEOpposite(children);
    
    EClass car = EcoreFactory.eINSTANCE.createEClass();
    pack.getEClassifiers().add(car);
    car.setName("Car");
    
    brand = EcoreFactory.eINSTANCE.createEAttribute();
    car.getEStructuralFeatures().add(brand);
    brand.setName("brand");
    brand.setEType(EcorePackage.eINSTANCE.getEString());

    cars = EcoreFactory.eINSTANCE.createEReference();
    person.getEStructuralFeatures().add(cars);
    cars.setName("cars");
    cars.setEType(car);
    cars.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
    cars.setContainment(true);
    
    john = pack.getEFactoryInstance().create(person);
    john.eSet(name, "John");
    
    mary = pack.getEFactoryInstance().create(person);
    mary.eSet(name, "Mary");
    
    herbie = pack.getEFactoryInstance().create(car);
    herbie.eSet(brand, "vw");
    
    ((List)john.eGet(children)).add(mary);
    assertEquals(john, mary.eGet(father));
    
    ((List)john.eGet(cars)).add(herbie);
    assertEquals(john, herbie.eContainer());
  }

  public void testOneTextFile() throws Exception
  {
    oneFileTest(new XMIResourceFactoryImpl());
  }

  public void testOneZipFile() throws Exception
  {
    oneFileTest(new XMIResourceFactoryImpl()
    {
      public Resource createResource(URI uri)
      {
        XMIResource xmiResource = (XMIResource)super.createResource(uri);
        xmiResource.setUseZip(true);
        return xmiResource;
      }
    });
  }

  public void testTwoTextFiles() throws Exception
  {
    twoFileTest(new XMIResourceFactoryImpl(), new XMIResourceFactoryImpl());
  }

  public void testOneTextAndOneZipFiles() throws Exception
  {
    twoFileTest(new XMIResourceFactoryImpl(), new XMIResourceFactoryImpl()
    {
      public Resource createResource(URI uri)
      {
        XMIResource xmiResource = (XMIResource)super.createResource(uri);
        xmiResource.setUseZip(true);
        return xmiResource;
      }
    });
  }
  
  public void testTwoZipFiles() throws Exception
  {
    Resource.Factory zipResourceFactory = new XMIResourceFactoryImpl()
    {
      public Resource createResource(URI uri)
      {
        XMIResource xmiResource = (XMIResource)super.createResource(uri);
        xmiResource.setUseZip(true);
        return xmiResource;
      }
    };
    
    twoFileTest(zipResourceFactory, zipResourceFactory);
  }  
  
  public void oneFileTest(Resource.Factory resourceFactory) throws Exception
  {
    URI uri = URI.createFileURI(TestUtil.getPluginDirectory() + "/people.pep");
    new File(uri.toFileString()).delete();

    Resource resource = resourceFactory.createResource(uri);
    
    resource.getContents().add(john);
    resource.getContents().add(mary);
    
    assertEquals(resource, john.eResource());
    assertEquals(resource, mary.eResource());
    
    resource.save(null);
    
    ResourceSet resourceSet = new ResourceSetImpl();
     resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("pep", resourceFactory);
    
    Resource loadedResource = resourceSet.getResource(uri, true);
    assertNotNull(loadedResource);
    assertEquals(2, loadedResource.getContents().size());
    
    checkIsJohn((EObject)loadedResource.getContents().get(0));
    checkIsMary((EObject)loadedResource.getContents().get(1));
    
    new File(uri.toFileString()).delete();
    assertFalse(new File(uri.toFileString()).exists());
  }

  public void twoFileTest(Resource.Factory johnResourceFactory, Resource.Factory maryResourceFactory) throws Exception
  {
    URI johnURI = URI.createFileURI(TestUtil.getPluginDirectory() + "/f1/people.john");
    new File(johnURI.toFileString()).delete();
    URI maryURI = URI.createFileURI(TestUtil.getPluginDirectory() + "/f1/f2/people.mary");
    new File(maryURI.toFileString()).delete();

    Resource johnResource = johnResourceFactory.createResource(johnURI);
    johnResource.getContents().add(john);
    
    Resource maryResource = maryResourceFactory.createResource(maryURI);
    maryResource.getContents().add(mary);
    
    assertEquals(johnResource, john.eResource());
    assertEquals(maryResource, mary.eResource());
    
    johnResource.save(null);
    maryResource.save(null);
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("john", johnResourceFactory);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("mary", maryResourceFactory);
    
    Resource loadedResource = resourceSet.getResource(johnURI, true);
    assertNotNull(loadedResource);
    assertEquals(1, loadedResource.getContents().size());
    
    EObject eObject = (EObject)loadedResource.getContents().get(0); 
    checkIsJohn(eObject);
    checkIsMary((EObject)((List)eObject.eGet(children)).get(0));
    
    resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("john", johnResourceFactory);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("mary", maryResourceFactory);
    
    loadedResource = resourceSet.getResource(maryURI, true);
    assertNotNull(loadedResource);
    assertEquals(1, loadedResource.getContents().size());
    
    eObject = (EObject)loadedResource.getContents().get(0); 
    checkIsMary(eObject);
    checkIsJohn((EObject)eObject.eGet(father));    
    
    new File(johnURI.toFileString()).delete();
    assertFalse(new File(johnURI.toFileString()).exists());
    new File(maryURI.toFileString()).delete();
    assertFalse(new File(maryURI.toFileString()).exists());
  }
  
  private void checkIsJohn(EObject person)
  {
    assertEquals(john.eGet(name), person.eGet(name));
    assertEquals(1, ((List)person.eGet(children)).size());
    assertEquals(mary.eGet(name), ((EObject)((List)person.eGet(children)).get(0)).eGet(name));
    assertEquals(1, ((List)person.eGet(cars)).size());
    assertEquals(herbie.eGet(brand), ((EObject)((List)person.eGet(cars)).get(0)).eGet(brand));
  }
  
  private void checkIsMary(EObject person)
  {
    assertEquals(mary.eGet(name), person.eGet(name));
    assertEquals(john.eGet(name), ((EObject)person.eGet(father)).eGet(name));
    assertTrue(((List)person.eGet(cars)).isEmpty());
  }
  
  public void testEDataType() throws Exception
  {
    EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
    pack.setName("localpack");
    pack.setNsPrefix("localpack");
    pack.setNsURI("http://mylocalpack");
    EPackage.Registry.INSTANCE.put(pack.getNsURI(), pack);
        
    EDataType date = EcoreFactory.eINSTANCE.createEDataType();
    pack.getEClassifiers().add(date);
    date.setName("Date");
    date.setInstanceClass(Date.class);
    date.setSerializable(true);

    EDataType foo = EcoreFactory.eINSTANCE.createEDataType();
    pack.getEClassifiers().add(foo);
    foo.setName("Foo");
    foo.setInstanceClassName("org.Foo");
    foo.setSerializable(true);
    
    EClass person  = EcoreFactory.eINSTANCE.createEClass();
    pack.getEClassifiers().add(person);
    person.setName("Person");
    
    EAttribute birthday = EcoreFactory.eINSTANCE.createEAttribute();
    person.getEStructuralFeatures().add(birthday);
    birthday.setName("birthday");
    birthday.setEType(date);

    long dateValue = System.currentTimeMillis();
    EObject john = pack.getEFactoryInstance().create(person);
    john.eSet(birthday, new Date(dateValue));
    
    XMIResource xmiResource = new XMIResourceImpl();
    xmiResource.setURI(URI.createFileURI("foo.xmi"));
    xmiResource.getContents().add(john);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    xmiResource.save(baos, null);
    
    XMIResource loadedXMIResource = new XMIResourceImpl();
    loadedXMIResource.load(new ByteArrayInputStream(baos.toByteArray()), null);
    assertEquals(1, loadedXMIResource.getContents().size());
    
    EObject loadedJohn = (EObject)loadedXMIResource.getContents().get(0);
    assertTrue(loadedJohn.eGet(birthday) instanceof Date);
    assertEquals(dateValue, ((Date)loadedJohn.eGet(birthday)).getTime());
  }
  
  /*
   * Bugzilla 126647
   */
  public void testCrossResourceContainment_XMLResourceUUID() throws Exception
  {
    EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
    pack.setName("pack");
    pack.setNsURI("http://www.eclipse.org/emf/pack/person");
    
    EClass person = EcoreFactory.eINSTANCE.createEClass();
    person.setName("Person");
    pack.getEClassifiers().add(person);

    EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
    name.setName("name");
    name.setEType(EcorePackage.eINSTANCE.getEString());
    person.getEStructuralFeatures().add(name);
    
    EReference children = EcoreFactory.eINSTANCE.createEReference();
    children.setName("children");
    children.setEType(person);
    children.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
    children.setContainment(true);
    children.setResolveProxies(true);
    person.getEStructuralFeatures().add(children);
    
    EObject john = EcoreUtil.create(person);
    john.eSet(name, "john");
    EObject mary = EcoreUtil.create(person);
    mary.eSet(name, "mary");
    ((List)john.eGet(children)).add(mary);
    
    assertNull(john.eResource());
    assertNull(mary.eResource());
    assertEquals(john, mary.eContainer());
    
    XMLResource johnResource = new XMLResourceImpl(URI.createFileURI("john"))
    {
      protected boolean useUUIDs()
      {
        return true;
      }
    };
    johnResource.setID(john, (String)john.eGet(name));
    johnResource.setID(mary, (String)mary.eGet(name));
    johnResource.getContents().add(john);
    
    assertEquals(john, johnResource.getEObject((String)john.eGet(name)));
    assertEquals(mary, johnResource.getEObject((String)mary.eGet(name)));
    //
    assertEquals(johnResource, john.eResource());
    assertEquals(johnResource, mary.eResource());
    assertEquals(john, mary.eContainer());
    
    XMLResource maryResource = new XMLResourceImpl(URI.createFileURI("mary"))
    {
      protected boolean useUUIDs()
      {
        return true;
      }
    };
    maryResource.getContents().add(mary);
    
    assertEquals(john, johnResource.getEObject((String)john.eGet(name)));
    assertNull(johnResource.getEObject((String)mary.eGet(name)));
    assertNull(maryResource.getEObject((String)john.eGet(name)));
    assertEquals(mary, maryResource.getEObject((String)mary.eGet(name)));
    //
    assertEquals(johnResource, john.eResource());
    assertEquals(maryResource, mary.eResource());
    assertEquals(john, mary.eContainer());
    
    maryResource.getContents().remove(mary);
    
    assertEquals(john, johnResource.getEObject((String)john.eGet(name)));
    assertEquals(mary, johnResource.getEObject((String)mary.eGet(name)));
    assertNull(maryResource.getEObject((String)john.eGet(name)));
    assertNull(maryResource.getEObject((String)mary.eGet(name)));
    //
    assertEquals(johnResource, john.eResource());
    assertEquals(johnResource, mary.eResource());
    assertEquals(john, mary.eContainer());
  }
  
  /*
   * Bugzilla 126650
   */
  public void testCrossResourceContainment_Dynamic_ResourceSet() throws Exception
  {
    EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
    pack.setName("pack");
    pack.setNsURI("http://www.eclipse.org/emf/pack/person");
    
    EClass person = EcoreFactory.eINSTANCE.createEClass();
    person.setName("Person");
    pack.getEClassifiers().add(person);

    EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
    name.setName("name");
    name.setEType(EcorePackage.eINSTANCE.getEString());
    person.getEStructuralFeatures().add(name);
    
    EReference children = EcoreFactory.eINSTANCE.createEReference();
    children.setName("children");
    children.setEType(person);
    children.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
    children.setContainment(true);
    children.setResolveProxies(true);
    person.getEStructuralFeatures().add(children);
    
    EReference spouse = EcoreFactory.eINSTANCE.createEReference();
    spouse.setName("spouse");
    spouse.setEType(person);
    spouse.setContainment(true);
    spouse.setResolveProxies(true);
    person.getEStructuralFeatures().add(spouse);
    
    EClass house = EcoreFactory.eINSTANCE.createEClass();
    house.setName("House");
    pack.getEClassifiers().add(house);
    
    EAttribute postalCode = EcoreFactory.eINSTANCE.createEAttribute();
    postalCode.setName("postalCode");
    postalCode.setEType(EcorePackage.eINSTANCE.getEString());
    house.getEStructuralFeatures().add(postalCode);

    EReference home = EcoreFactory.eINSTANCE.createEReference();
    home.setName("home");
    home.setEType(house);
    home.setContainment(true);
    home.setResolveProxies(true);
    person.getEStructuralFeatures().add(home);

    EReference owner = EcoreFactory.eINSTANCE.createEReference();
    owner.setName("owner");
    owner.setEType(person);
    house.getEStructuralFeatures().add(owner);
    
    owner.setEOpposite(home);
    home.setEOpposite(owner);
    
    EReference houses = EcoreFactory.eINSTANCE.createEReference();
    houses.setName("houses");
    houses.setEType(house);
    houses.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
    houses.setContainment(true);
    houses.setResolveProxies(true);
    person.getEStructuralFeatures().add(houses);
    
    EReference landlord  = EcoreFactory.eINSTANCE.createEReference();
    landlord.setName("landlord");
    landlord.setEType(person);
    house.getEStructuralFeatures().add(landlord);

    landlord.setEOpposite(houses);
    houses.setEOpposite(landlord);
        
       
    EObject john = EcoreUtil.create(person);
    john.eSet(name, "john");
    EObject jane = EcoreUtil.create(person);
    jane.eSet(name, "jane");
    john.eSet(spouse, jane);
    EObject mary = EcoreUtil.create(person);
    mary.eSet(name, "mary");
    ((List)john.eGet(children)).add(mary);
    EObject johnsHome = EcoreUtil.create(house);
    johnsHome.eSet(postalCode, "abcdefg");
    john.eSet(home, johnsHome);
    EObject house1 = EcoreUtil.create(house);
    house1.eSet(postalCode, "house 1");
    ((List)john.eGet(houses)).add(house1);
    
    assertNull(john.eResource());
    assertNull(jane.eResource());
    assertNull(mary.eResource());
    assertNull(johnsHome.eResource());
    assertNull(house1.eResource());
    assertEquals(john, jane.eContainer());
    assertEquals(john, mary.eContainer());
    assertEquals(john, johnsHome.eContainer());
    assertEquals(john, johnsHome.eGet(owner));
    assertEquals(john, house1.eContainer());
    assertEquals(john, house1.eGet(landlord));

    URI johnURI = URI.createFileURI("john.xmi");
    URI uri2 = URI.createFileURI("uri2.xmi");
    
    Resource johnResource = new XMIResourceImpl(johnURI);
    johnResource.getContents().add(john);
    
    assertEquals(johnResource, john.eResource());
    assertEquals(johnResource, jane.eResource());
    assertEquals(johnResource, mary.eResource());
    assertEquals(johnResource, johnsHome.eResource());
    assertEquals(johnResource, house1.eResource());
    assertEquals(john, jane.eContainer());
    assertEquals(john, mary.eContainer());
    assertEquals(john, johnsHome.eContainer());
    assertEquals(john, johnsHome.eGet(owner));
    assertEquals(john, house1.eContainer());
    assertEquals(john, house1.eGet(landlord));
    
    Resource resource2 = new XMIResourceImpl(uri2);
    resource2.getContents().add(jane);
    resource2.getContents().add(mary);
    resource2.getContents().add(johnsHome);
    resource2.getContents().add(house1);

    assertEquals(johnResource, john.eResource());
    assertEquals(resource2, jane.eResource());
    assertEquals(resource2, mary.eResource());
    assertEquals(resource2, johnsHome.eResource());
    assertEquals(resource2, house1.eResource());
    assertEquals(john, jane.eContainer());
    assertEquals(john, mary.eContainer());
    assertEquals(john, johnsHome.eContainer());
    assertEquals(john, johnsHome.eGet(owner));
    assertEquals(john, house1.eContainer());
    assertEquals(john, house1.eGet(landlord));
    
    ByteArrayOutputStream johnBAOS = new ByteArrayOutputStream();
    johnResource.save(johnBAOS, null);
    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    resource2.save(baos2, null);
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(pack.getNsURI(), pack);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    
    johnResource = resourceSet.createResource(johnURI);
    johnResource.load(new ByteArrayInputStream(johnBAOS.toByteArray()), null);
    resource2 = resourceSet.createResource(uri2);
    resource2.load(new ByteArrayInputStream(baos2.toByteArray()), null);
    
    john = (EObject)johnResource.getContents().get(0);
    jane = (EObject)resource2.getContents().get(0);
    mary = (EObject)resource2.getContents().get(1);
    johnsHome = (EObject)resource2.getContents().get(2);
    house1 = (EObject)resource2.getContents().get(3);

    assertEquals("john", john.eGet(name));
    assertEquals("jane", jane.eGet(name));
    assertEquals("mary", mary.eGet(name));
    assertEquals("abcdefg", johnsHome.eGet(postalCode));
    assertEquals("house 1", house1.eGet(postalCode));
    EcoreUtil.resolveAll(johnResource);
    
    assertEquals(johnResource, john.eResource());
    assertEquals(resource2, jane.eResource());
    assertEquals(resource2, mary.eResource());
    assertEquals(resource2, johnsHome.eResource());
    assertEquals(resource2, house1.eResource());
    assertEquals(john, jane.eContainer());
    assertEquals(john, mary.eContainer());
    assertEquals(john, johnsHome.eContainer());
    assertEquals(john, johnsHome.eGet(owner));
    assertEquals(john, house1.eContainer());
    assertEquals(john, house1.eGet(landlord));    
  }
  
  /*
   * Bugzilla 126650
   */
  public void testCrossResourceContainment_Static_ResourceSet() throws Exception
  {
    Library library = LibFactory.eINSTANCE.createLibrary();
    library.setName("Public Library");
    //
    Book book = LibFactory.eINSTANCE.createBook();
    book.setTitle("EMF");
    library.getBooks().add(book);
    //
    Address libraryAddress = LibFactory.eINSTANCE.createAddress();
    libraryAddress.setPostalCode("abcdefg");
    library.setAddress(libraryAddress);
    //
    Person john = LibFactory.eINSTANCE.createPerson();
    john.setName("john");
    library.getWriters().add(john);
    //
    Cafeteria cafeteria = LibFactory.eINSTANCE.createCafeteria();
    cafeteria.setName("cafe");
    library.setCafeteria(cafeteria);
    
    assertNull(library.eResource());
    assertNull(book.eResource());
    assertNull(libraryAddress.eResource());
    assertNull(john.eResource());
    assertNull(cafeteria.eResource());
    assertEquals(library, book.eContainer());
    assertEquals(library, libraryAddress.eContainer());
    assertEquals(library, john.eContainer());
    assertEquals(library, john.getLibrary());
    assertEquals(library, cafeteria.eContainer());
    assertEquals(library, cafeteria.getLibrary());
    
    URI libraryURI = URI.createFileURI("library.xmi");
    URI childrenURI = URI.createFileURI("children.xmi");
    
    Resource libraryResource = new XMIResourceImpl(libraryURI);
    libraryResource.getContents().add(library);
    
    assertEquals(libraryResource, library.eResource());
    assertEquals(libraryResource, book.eResource());
    assertEquals(libraryResource, libraryAddress.eResource());
    assertEquals(libraryResource, john.eResource());
    assertEquals(libraryResource, cafeteria.eResource());
    assertEquals(library, book.eContainer());
    assertEquals(library, libraryAddress.eContainer());
    assertEquals(library, john.eContainer());
    assertEquals(library, john.getLibrary());
    assertEquals(library, cafeteria.eContainer());
    assertEquals(library, cafeteria.getLibrary());
    
    Resource childrenResource = new XMIResourceImpl(childrenURI);
    childrenResource.getContents().add(book);
    childrenResource.getContents().add(libraryAddress);
    childrenResource.getContents().add(john);
    childrenResource.getContents().add(cafeteria);

    assertEquals(libraryResource, library.eResource());
    assertEquals(childrenResource, book.eResource());
    assertEquals(childrenResource, libraryAddress.eResource());
    assertEquals(childrenResource, john.eResource());
    assertEquals(childrenResource, cafeteria.eResource());
    assertEquals(library, book.eContainer());
    assertEquals(library, libraryAddress.eContainer());
    assertEquals(library, john.eContainer());
    assertEquals(library, john.getLibrary());
    assertEquals(library, cafeteria.eContainer());
    assertEquals(library, cafeteria.getLibrary());
    
    ByteArrayOutputStream libraryBAOS = new ByteArrayOutputStream();
    libraryResource.save(libraryBAOS, null);
    ByteArrayOutputStream bookBAOS = new ByteArrayOutputStream();
    childrenResource.save(bookBAOS, null);
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    
    libraryResource = resourceSet.createResource(libraryURI);
    libraryResource.load(new ByteArrayInputStream(libraryBAOS.toByteArray()), null);
    childrenResource = resourceSet.createResource(childrenURI);
    childrenResource.load(new ByteArrayInputStream(bookBAOS.toByteArray()), null);
    
    library = (Library)libraryResource.getContents().get(0);
    book = (Book)childrenResource.getContents().get(0);
    libraryAddress = (Address)childrenResource.getContents().get(1);
    john = (Person)childrenResource.getContents().get(2);
    cafeteria = (Cafeteria)childrenResource.getContents().get(3);

    assertEquals("Public Library", library.getName());
    assertEquals("EMF", book.getTitle());
    assertEquals("abcdefg", libraryAddress.getPostalCode());
    assertEquals("john", john.getName());
    assertEquals("cafe", cafeteria.getName());
    EcoreUtil.resolveAll(libraryResource);
    
    assertEquals(libraryResource, library.eResource());
    assertEquals(childrenResource, book.eResource());
    assertEquals(childrenResource, libraryAddress.eResource());
    assertEquals(childrenResource, john.eResource());
    assertEquals(childrenResource, cafeteria.eResource());
    assertEquals(library, book.eContainer());   
    assertEquals(library, libraryAddress.eContainer());
    assertEquals(library, john.eContainer());
    assertEquals(library, john.getLibrary());
    assertEquals(library, cafeteria.eContainer());
    assertEquals(library, cafeteria.getLibrary());
  }
  
  /*
   * Bugzilla 132904
   */
  public void testCrossResourceContainment_RemoveChild() throws Exception
  {
    Library library = LibFactory.eINSTANCE.createLibrary();
    library.setName("Public Library");
    //
    Book book = LibFactory.eINSTANCE.createBook();
    book.setTitle("EMF");
    library.getBooks().add(book);
    
    Resource libResource = new ResourceImpl(URI.createURI("lib"));
    libResource.getContents().add(library);
    
    assertEquals(library, book.eContainer());
    assertEquals(libResource, library.eResource());
    assertEquals(libResource, book.eResource());

    Resource bookResource = new ResourceImpl(URI.createURI("book"));
    bookResource.getContents().add(book);

    assertEquals(library, book.eContainer());
    assertEquals(libResource, library.eResource());
    assertEquals(bookResource, book.eResource());
    
    library.getBooks().remove(book);

    assertNull(book.eContainer());
    assertEquals(libResource, library.eResource());
    assertEquals(bookResource, book.eResource());
    
    library.getBooks().add(book);

    assertEquals(library, book.eContainer());
    assertEquals(libResource, library.eResource());
    assertEquals(bookResource, book.eResource());
  }
}
