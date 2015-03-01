<?php

namespace Insalade\CommunicationBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

/**
 * Push
 *
 * @ORM\Table(name="push")
 * @ORM\Entity
 * @ORM\HasLifecycleCallbacks
 */
class Push
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id_push", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="association", type="string", length=255)
     */
    private $association;

    /**
     * @var string
     *
     * @ORM\Column(name="title", type="string", length=255)
     */
    private $title;

    /**
     * @var string
     *
     * @ORM\Column(name="push_text", type="string", length=255)
     */
    private $pushText;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="date_start", type="datetime")
     */
    private $dateStart;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="date_create", type="datetime")
     */
    private $creationDate;

    /**
     * @var string
     *
     * @ORM\Column(name="state", type="string", length=255)
     */
    private $state;

    public function __construct()
    {
        $this->setState("waiting");
        $this->setCreationDate(new \DateTime());
    }

    /**
     * Get id
     *
     * @return integer 
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set association
     *
     * @param string $association
     * @return Push
     */
    public function setAssociation($association)
    {
        $this->association = $association;

        return $this;
    }

    /**
     * Get association
     *
     * @return string 
     */
    public function getAssociation()
    {
        return $this->association;
    }

    /**
     * Set title
     *
     * @param string $title
     * @return Push
     */
    public function setTitle($title)
    {
        $this->title = $title;

        return $this;
    }

    /**
     * Get title
     *
     * @return string 
     */
    public function getTitle()
    {
        return $this->title;
    }

    /**
     * Set pushText
     *
     * @param string $pushText
     * @return Push
     */
    public function setPushText($pushText)
    {
        $this->pushText = $pushText;

        return $this;
    }

    /**
     * Get pushText
     *
     * @return string 
     */
    public function getPushText()
    {
        return $this->pushText;
    }

    /**
     * Set dateStart
     *
     * @param \DateTime $dateStart
     * @return Push
     */
    public function setDateStart($dateStart)
    {
        $this->dateStart = $dateStart;

        return $this;
    }

    /**
     * Get dateStart
     *
     * @return \DateTime 
     */
    public function getDateStart()
    {
        return $this->dateStart;
    }

    /**
     * Set state
     *
     * @param string $state
     * @return Push
     */
    public function setState($state)
    {
        if($state == 'validated' OR $state == 'waiting' OR $state == 'rejected' OR $state == 'pushed')
            $this->state = $state;

        return $this;
    }

    /**
     * Get state
     *
     * @return string 
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * Set creationDate
     *
     * @param \DateTime $creationDate
     * @return Push
     */
    public function setCreationDate($creationDate)
    {
        $this->creationDate = $creationDate;

        return $this;
    }

    /**
     * Get creationDate
     *
     * @return \DateTime 
     */
    public function getCreationDate()
    {
        return $this->creationDate;
    }
}
